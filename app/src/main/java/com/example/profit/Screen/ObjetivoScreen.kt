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
import com.example.profit.Model.Objetivo
import com.example.profit.ViewModel.ObjetivoViewModel
import com.example.profit.ui.components.BarraSuperior
import com.example.profit.ui.components.DrawerItem
import com.example.profit.ui.components.MenuLateral
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjetivoScreen(navController: NavHostController, viewModel: ObjetivoViewModel = viewModel()) {
    val objetivos by viewModel.objetivos.observeAsState(emptyList())
    var codigoBusqueda by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedObjetivo by remember { mutableStateOf<Objetivo?>(null) }

    // Para el control de elementos expandidos
    val expandedItemIds = remember { mutableStateOf<Set<Long>>(emptySet()) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Cargar objetivos al iniciar
    LaunchedEffect(true) {
        viewModel.listarObjetivos()
    }

    val menuItems = listOf(
        DrawerItem("Usuarios", Screens.Usuario.route, Icons.Default.Person),
        DrawerItem("Objetivos", Screens.Objetivo.route, Icons.Default.FitnessCenter),
        DrawerItem("Ingredientes", Screens.Ingrediente.route, Icons.Default.Restaurant),
        DrawerItem("Categorías", Screens.Categoria.route, Icons.Default.Category),
        DrawerItem("Recetas", Screens.Receta.route, Icons.Default.RestaurantMenu),
        DrawerItem("Agregar Recetas", Screens.AgregarReceta.route, Icons.Default.Add),
        DrawerItem("Ingredientes de Recetas", Screens.RecetaIngrediente.route, Icons.Default.AllInclusive)
    )

    // Mejorado el filtrado para buscar tanto por ID como por nombre
    val objetivosFiltrados = objetivos.filter {
        it.idObjetivo?.toString()?.contains(codigoBusqueda, ignoreCase = true) == true ||
                it.objetivo.contains(codigoBusqueda, ignoreCase = true)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuLateral(
                drawerState = drawerState,
                items = menuItems,
                onItemClick = { item ->
                    scope.launch { drawerState.close() }
                    navController.navigate(item.route)
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                BarraSuperior(
                    title = "Objetivos",
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(
                                contentDescription = "Abrir menú",
                                imageVector = Icons.Default.Menu
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        selectedObjetivo = null
                        showAddDialog = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar objetivo")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Objetivos",
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

                if (objetivosFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No hay objetivos disponibles.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(objetivosFiltrados) { objetivoItem ->
                            val isExpanded = expandedItemIds.value.contains(objetivoItem.idObjetivo ?: 0)
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
                                            newSet.remove(objetivoItem.idObjetivo ?: 0)
                                        } else {
                                            newSet.add(objetivoItem.idObjetivo ?: 0)
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
                                                text = objetivoItem.objetivo,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                text = "Código: ${objetivoItem.idObjetivo}",
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
                                                text = "Detalles del objetivo",
                                                style = MaterialTheme.typography.titleSmall
                                            )

                                            Spacer(modifier = Modifier.height(16.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Button(
                                                    onClick = {
                                                        selectedObjetivo = objetivoItem
                                                        showEditDialog = true
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
                                                        selectedObjetivo = objetivoItem
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

    // Dialog para eliminar objetivo
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmación") },
            text = { Text("¿Está seguro que desea eliminar este objetivo?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedObjetivo?.let {
                        viewModel.eliminarObjetivo(it.idObjetivo!!)
                        Toast.makeText(context, "Objetivo eliminado", Toast.LENGTH_SHORT).show()
                        viewModel.listarObjetivos()
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

    // Dialog para agregar objetivo
    if (showAddDialog) {
        ObjetivoDialog(
            onDismiss = { showAddDialog = false },
            onSave = { nuevoObjetivo ->
                scope.launch {
                    viewModel.guardarObjetivo(nuevoObjetivo)
                    viewModel.listarObjetivos()
                    Toast.makeText(context, "Objetivo creado correctamente", Toast.LENGTH_SHORT).show()
                }
                showAddDialog = false
            }
        )
    }

    // Dialog para editar objetivo
    if (showEditDialog && selectedObjetivo != null) {
        ObjetivoDialog(
            objetivo = selectedObjetivo,
            onDismiss = { showEditDialog = false },
            onSave = { objetivoEditado ->
                scope.launch {
                    viewModel.guardarObjetivo(objetivoEditado)
                    viewModel.listarObjetivos()
                    Toast.makeText(context, "Objetivo actualizado correctamente", Toast.LENGTH_SHORT).show()
                }
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ObjetivoDialog(
    objetivo: Objetivo? = null,
    onDismiss: () -> Unit,
    onSave: (Objetivo) -> Unit
) {
    var nombre by remember { mutableStateOf(objetivo?.objetivo ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (objetivo == null) "Agregar Objetivo" else "Editar Objetivo") },
        text = {
            Column {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del objetivo") },
                    leadingIcon = { Icon(Icons.Filled.FitnessCenter, contentDescription = "Objetivo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombre.isBlank()
                )

                if (nombre.isBlank()) {
                    Text(
                        text = "El objetivo es obligatorio",
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
                        val objetivoData = objetivo?.copy(
                            objetivo = nombre
                        ) ?: Objetivo(
                            objetivo = nombre
                        )
                        onSave(objetivoData)
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