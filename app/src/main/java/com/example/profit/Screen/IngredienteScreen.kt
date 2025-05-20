package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.profit.Model.Ingrediente
import com.example.profit.ViewModel.IngredienteViewModel
import com.example.profit.ui.components.BarraSuperior
import com.example.profit.ui.components.DrawerItem
import com.example.profit.ui.components.MenuLateral
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun IngredienteScreen(
    navController: NavHostController,
    viewModel: IngredienteViewModel = viewModel()
) {
    val ingredientes by viewModel.ingredientes.observeAsState(emptyList())
    var codigoBusqueda by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showIngredienteDialog by remember { mutableStateOf(false) }
    var selectedIngrediente by remember { mutableStateOf<Ingrediente?>(null) }

    // Para el control de elementos expandidos
    val expandedItemIds = remember { mutableStateOf<Set<Long>>(emptySet()) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Estado del Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scopeDrawer = rememberCoroutineScope()

    // Items para el menú lateral
    val menuItems = listOf(
        DrawerItem("Usuarios", Screens.Usuario.route, Icons.Default.Person),
        DrawerItem("Objetivos", Screens.Objetivo.route, Icons.Default.FitnessCenter),
        DrawerItem("Ingredientes", Screens.Ingrediente.route, Icons.Default.Restaurant),
        DrawerItem("Categorías", Screens.Categoria.route, Icons.Default.Category),
        DrawerItem("Recetas", Screens.Receta.route, Icons.Default.RestaurantMenu),
        DrawerItem("Agregar Recetas", Screens.AgregarReceta.route, Icons.Default.Add),
        DrawerItem("Ingredientes de Recetas", Screens.RecetaIngrediente.route, Icons.Default.AllInclusive)
    )

    LaunchedEffect(true) {
        viewModel.listarIngredientes()
    }

    // Mejorado el filtrado para buscar tanto por ID como por nombre
    val ingredientesFiltrados = ingredientes?.filter {
        it.idIngrediente?.toString()?.contains(codigoBusqueda, ignoreCase = true) == true ||
                it.ingrediente.contains(codigoBusqueda, ignoreCase = true)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuLateral(
                drawerState = drawerState,
                items = menuItems,
                onItemClick = { item ->
                    scopeDrawer.launch { drawerState.close() }
                    navController.navigate(item.route)
                }
            )
        }
    ) {
        // Contenido principal con barra superior
        Scaffold(
            topBar = {
                BarraSuperior(
                    title = "Ingredientes",
                    navigationIcon = {
                        IconButton(onClick = {
                            scopeDrawer.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        selectedIngrediente = null
                        showIngredienteDialog = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar ingrediente")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Ingredientes",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = codigoBusqueda,
                    onValueChange = { codigoBusqueda = it },
                    label = { Text("Buscar por código o nombre") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar Icon") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                if (ingredientesFiltrados != null) {
                    if (ingredientesFiltrados.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No hay ingredientes disponibles.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(ingredientesFiltrados) { ingredienteItem ->
                                val isExpanded = expandedItemIds.value.contains(ingredienteItem.idIngrediente ?: 0)
                                val rotationState = animateFloatAsState(
                                    targetValue = if (isExpanded) 180f else 0f
                                )

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            val newSet = expandedItemIds.value.toMutableSet()
                                            if (isExpanded) {
                                                newSet.remove(ingredienteItem.idIngrediente ?: 0)
                                            } else {
                                                newSet.add(ingredienteItem.idIngrediente ?: 0)
                                            }
                                            expandedItemIds.value = newSet
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = ingredienteItem.ingrediente,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Text(
                                                    text = "Código: ${ingredienteItem.idIngrediente}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Expandir",
                                                modifier = Modifier.rotate(rotationState.value)
                                            )
                                        }

                                        AnimatedVisibility(visible = isExpanded) {
                                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                                Text(
                                                    text = "Detalles del ingrediente",
                                                    style = MaterialTheme.typography.titleSmall
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(
                                                    text = "Unidad: ${ingredienteItem.unidad}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            selectedIngrediente = ingredienteItem
                                                            showIngredienteDialog = true
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = MaterialTheme.colorScheme.primary
                                                        )
                                                    ) {
                                                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Editar")
                                                    }

                                                    Button(
                                                        onClick = {
                                                            selectedIngrediente = ingredienteItem
                                                            showDeleteDialog = true
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = MaterialTheme.colorScheme.error
                                                        )
                                                    ) {
                                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Eliminar")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog para eliminar ingrediente
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmación") },
            text = { Text("¿Está seguro que desea eliminar este ingrediente?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedIngrediente?.let {
                        viewModel.eliminarIngrediente(it.idIngrediente!!)
                        Toast.makeText(context, "Ingrediente eliminado", Toast.LENGTH_SHORT).show()
                        viewModel.listarIngredientes()
                    }
                    showDeleteDialog = false
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // Dialog unificado para agregar/editar ingrediente
    if (showIngredienteDialog) {
        IngredienteDialog(
            ingrediente = selectedIngrediente,
            onDismiss = { showIngredienteDialog = false },
            onSave = { ingredienteGuardar ->
                scope.launch {
                    viewModel.guardarIngrediente(ingredienteGuardar)
                    viewModel.listarIngredientes()
                    Toast.makeText(
                        context,
                        if (selectedIngrediente == null) "Ingrediente agregado" else "Ingrediente actualizado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                showIngredienteDialog = false
            }
        )
    }
}

@Composable
fun IngredienteDialog(
    ingrediente: Ingrediente? = null,
    onDismiss: () -> Unit,
    onSave: (Ingrediente) -> Unit
) {
    var nombre by remember { mutableStateOf(ingrediente?.ingrediente ?: "") }
    var unidad by remember { mutableStateOf(ingrediente?.unidad ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (ingrediente == null) "Agregar Ingrediente" else "Editar Ingrediente") },
        text = {
            Column {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del ingrediente") },
                    leadingIcon = { Icon(Icons.Filled.Restaurant, contentDescription = "Ingrediente") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombre.isBlank()
                )

                if (nombre.isBlank()) {
                    Text(
                        text = "El ingrediente es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = unidad,
                    onValueChange = { unidad = it },
                    label = { Text("Unidad de medida") },
                    leadingIcon = { Icon(Icons.Filled.LineWeight, contentDescription = "Unidad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = unidad.isBlank()
                )

                if (unidad.isBlank()) {
                    Text(
                        text = "La unidad es obligatoria",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nombre.isNotEmpty() && unidad.isNotEmpty()) {
                        val ingredienteData = ingrediente?.copy(
                            ingrediente = nombre,
                            unidad = unidad
                        ) ?: Ingrediente(
                            ingrediente = nombre,
                            unidad = unidad
                        )
                        onSave(ingredienteData)
                    }
                },
                enabled = nombre.isNotEmpty() && unidad.isNotEmpty()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}