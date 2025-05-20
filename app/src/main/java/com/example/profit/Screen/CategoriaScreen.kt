package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.profit.Model.Categoria
import com.example.profit.ViewModel.CategoriaViewModel
import com.example.profit.ui.components.BarraSuperior
import com.example.profit.ui.components.DrawerItem
import com.example.profit.ui.components.MenuLateral
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip

@Composable
fun CategoriaScreen(navController: NavHostController, viewModel: CategoriaViewModel = viewModel()) {
    val categorias by viewModel.categorias.observeAsState(emptyList())
    var codigoBusqueda by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCategoriaDialog by remember { mutableStateOf(false) }
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) }

    // Para el control de elementos expandidos
    val expandedItemIds = remember { mutableStateOf<Set<Long>>(emptySet()) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(true) {
        viewModel.listarCategorias()
    }

    val menuItems = listOf(
        DrawerItem("Usuarios", Screens.Usuario.route, Icons.Default.Person),
        DrawerItem("Objetivos", Screens.Objetivo.route, Icons.Default.FitnessCenter),
        DrawerItem("Ingredientes", Screens.Ingrediente.route, Icons.Default.Restaurant),
        DrawerItem("Categorías", Screens.Categoria.route, Icons.Default.Category),
        DrawerItem("Recetas", Screens.Receta.route, Icons.Default.RestaurantMenu),
        // DrawerItem("Agregar Recetas", Screens.AgregarReceta.route, Icons.Default.Add),
        DrawerItem("Ingredientes de Recetas", Screens.RecetaIngrediente.route, Icons.Default.AllInclusive)
    )

    // Mejorado el filtrado para buscar tanto por ID como por nombre
    val categoriasFiltradas = categorias?.filter {
        it.idCategoria?.toString()?.contains(codigoBusqueda, ignoreCase = true) == true ||
                it.categoria.contains(codigoBusqueda, ignoreCase = true)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuLateral(
                drawerState = drawerState,
                items = menuItems,
                onItemClick = {
                    navController.navigate(it.route)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                BarraSuperior(
                    title = "Categorías",
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        selectedCategoria = null
                        showCategoriaDialog = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar categoría")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = codigoBusqueda,
                    onValueChange = { codigoBusqueda = it },
                    label = { Text("Buscar receta") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp)
                )

                if (categoriasFiltradas != null) {
                    if (categoriasFiltradas.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No hay categorías disponibles.")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(categoriasFiltradas) { categoriaItem ->
                                val isExpanded = expandedItemIds.value.contains(categoriaItem.idCategoria ?: 0)
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
                                                newSet.remove(categoriaItem.idCategoria ?: 0)
                                            } else {
                                                newSet.add(categoriaItem.idCategoria ?: 0)
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
                                                    text = categoriaItem.categoria,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Text(
                                                    text = "Código: ${categoriaItem.idCategoria}",
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
                                                    text = "Detalles de la categoría",
                                                    style = MaterialTheme.typography.titleSmall
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            selectedCategoria = categoriaItem
                                                            showCategoriaDialog = true
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
                                                            selectedCategoria = categoriaItem
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

    // Dialog para eliminar categoría
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmación") },
            text = { Text("¿Está seguro que desea eliminar esta categoría?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedCategoria?.let {
                            viewModel.eliminarCategoria(it.idCategoria!!)
                            Toast.makeText(context, "Categoría eliminada", Toast.LENGTH_SHORT).show()
                            viewModel.listarCategorias()
                        }
                        showDeleteDialog = false
                    }
                ) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // Dialog para agregar/editar categoría
    if (showCategoriaDialog) {
        CategoriaDialog(
            categoria = selectedCategoria,
            onDismiss = { showCategoriaDialog = false },
            onSave = { categoriaGuardar ->
                scope.launch {
                    viewModel.guardarCategoria(categoriaGuardar)
                    viewModel.listarCategorias()
                    Toast.makeText(
                        context,
                        if (selectedCategoria == null) "Categoría agregada" else "Categoría actualizada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                showCategoriaDialog = false
            }
        )
    }
}

@Composable
fun CategoriaDialog(
    categoria: Categoria? = null,
    onDismiss: () -> Unit,
    onSave: (Categoria) -> Unit
) {
    var nombre by remember { mutableStateOf(categoria?.categoria ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (categoria == null) "Agregar Categoría" else "Editar Categoría") },
        text = {
            Column {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la categoría") },
                    leadingIcon = { Icon(Icons.Filled.Category, contentDescription = "Categoría") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombre.isBlank()
                )

                if (nombre.isBlank()) {
                    Text(
                        text = "La categoría es obligatoria",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nombre.isNotEmpty()) {
                        val categoriaData = categoria?.copy(
                            categoria = nombre
                        ) ?: Categoria(
                            categoria = nombre
                        )
                        onSave(categoriaData)
                    }
                },
                enabled = nombre.isNotEmpty()
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