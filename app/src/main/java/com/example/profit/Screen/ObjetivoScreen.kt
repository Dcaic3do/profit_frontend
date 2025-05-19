package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.profit.Model.Objetivo
import com.example.profit.ViewModel.ObjetivoViewModel
import com.example.profit.ui.components.BarraSuperior
import com.example.profit.ui.components.MenuLateral
import com.example.profit.ui.components.DrawerItem
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjetivoScreen(navController: NavHostController, viewModel: ObjetivoViewModel = viewModel()) {
    val objetivos by viewModel.objetivos.observeAsState(emptyList())
    var objetivo by remember { mutableStateOf("") }
    var codigoBusqueda by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var objetivoEliminar by remember { mutableStateOf<Objetivo?>(null) }

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
        DrawerItem("Agregar Recetas", Screens.AgregarReceta.route, Icons.Default.Add)
    )

    val objetivosFiltrados = objetivos.filter {
        it.idObjetivo?.toString()?.contains(codigoBusqueda, ignoreCase = true) ?: false
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
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                contentDescription = "Abrir menú",
                                imageVector = Icons.Default.Menu,
                                  tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Agregar Nuevo Objetivo",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )

                TextField(
                    value = objetivo,
                    onValueChange = { objetivo = it },
                    label = { Text("Objetivo") },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = "Objetivo")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    isError = objetivo.isBlank()
                )

                if (objetivo.isBlank()) {
                    Text(
                        text = "El objetivo es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        if (objetivo.isNotEmpty()) {
                            scope.launch {
                                viewModel.guardarObjetivo(Objetivo(objetivo = objetivo))
                                objetivo = ""
                                Toast.makeText(context, "Objetivo insertado", Toast.LENGTH_SHORT).show()
                                viewModel.listarObjetivos()
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp),
                    enabled = objetivo.isNotEmpty()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Objetivo")
                }

                TextField(
                    value = codigoBusqueda,
                    onValueChange = { codigoBusqueda = it },
                    label = { Text("Buscar por código") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (objetivosFiltrados.isEmpty()) {
                    Text(text = "No hay objetivos disponibles.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(objetivosFiltrados) { objetivoItem ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Código: ${objetivoItem.idObjetivo}")
                                    Text("Objetivo: ${objetivoItem.objetivo}")

                                    Button(
                                        onClick = {
                                            objetivoEliminar = objetivoItem
                                            showDialog = true
                                        },
                                        modifier = Modifier.padding(top = 8.dp)
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmación") },
            text = { Text("¿Está seguro que desea eliminar este objetivo?") },
            confirmButton = {
                TextButton(onClick = {
                    objetivoEliminar?.let {
                        viewModel.eliminarObjetivo(it.idObjetivo!!)
                        Toast.makeText(context, "Objetivo eliminado", Toast.LENGTH_SHORT).show()
                        viewModel.listarObjetivos()
                    }
                    showDialog = false
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}
