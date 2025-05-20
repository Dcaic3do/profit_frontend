package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LineWeight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.profit.Model.Ingrediente
import com.example.profit.Model.Objetivo
import com.example.profit.Model.Receta
import com.example.profit.Model.RecetaIngrediente
import com.example.profit.Model.Usuario
import com.example.profit.ViewModel.IngredienteViewModel
import com.example.profit.ViewModel.ObjetivoViewModel
import com.example.profit.ViewModel.RecetaIngredienteViewModel
import com.example.profit.ViewModel.RecetaViewModel
import com.example.profit.ui.components.BarraSuperior
import com.example.profit.ui.components.DrawerItem
import com.example.profit.ui.components.MenuLateral
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetaIngredienteScreen(navController: NavHostController, viewModel: RecetaIngredienteViewModel = viewModel()) {
    val recetaIngredientes by viewModel.recetaIngredientes.observeAsState(emptyList())
    var recetaIngrediente by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var codigoBusqueda by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var recetaIngredienteEliminar by remember { mutableStateOf<RecetaIngrediente?>(null) }

    //Lista Desplegable Receta
    val recetaViewModel: RecetaViewModel = viewModel()
    val listarReceta by recetaViewModel.recetas.observeAsState(emptyList())
    var recetaSeleccionada by remember { mutableStateOf<Receta?>(null) }

    //Lista Desplegable Ingrediente
    val ingredienteViewModel: IngredienteViewModel = viewModel()
    val listarIngredientes by ingredienteViewModel.ingredientes.observeAsState(emptyList())
    var ingredienteSeleccionado by remember { mutableStateOf<Ingrediente?>(null) }

    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Cargar listas al iniciar
    LaunchedEffect(true) {
        viewModel.listarRecetaIngredientes()
        recetaViewModel.listarRecetas()
        ingredienteViewModel.listarIngredientes()
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

    val recetaIngredientesFiltrados = recetaIngredientes?.filter {
        it.idRecetaIngrediente?.toString()?.contains(codigoBusqueda, ignoreCase = true) ?: false
    } ?: emptyList()

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
                    text = "Agregar Ingrediente a una Receta",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Lista Desplegable Recetas
                var expandedRecetas by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expandedRecetas,
                    onExpandedChange = { expandedRecetas = !expandedRecetas },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = recetaSeleccionada?.receta ?: "",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Filled.Restaurant, contentDescription = "Restaurant Icon")
                        },
                        label = { Text("Seleccionar Receta") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRecetas)
                        },
                        modifier = Modifier
                            .menuAnchor() // Este ancla el dropdown justo debajo del TextField
                            .fillMaxWidth(),
                        isError = recetaSeleccionada == null
                    )

                    ExposedDropdownMenu(
                        expanded = expandedRecetas,
                        onDismissRequest = { expandedRecetas = false }
                    ) {
                        listarReceta?.forEach { receta ->
                            DropdownMenuItem(
                                text = { Text(receta.receta) },
                                onClick = {
                                    recetaSeleccionada = receta
                                    expandedRecetas = false
                                }
                            )
                        }
                    }
                }

                if (recetaSeleccionada == null) {
                    Text(
                        text = "Seleccionar una receta es obligatoria",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Lista Desplegable Recetas
                var expandedIngredientes by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expandedIngredientes,
                    onExpandedChange = { expandedIngredientes = !expandedIngredientes },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = ingredienteSeleccionado?.ingrediente ?: "",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(Icons.Filled.RestaurantMenu, contentDescription = "Ingredient Icon")
                        },
                        label = { Text("Seleccionar Ingrediente") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIngredientes)
                        },
                        modifier = Modifier
                            .menuAnchor() // Este ancla el dropdown justo debajo del TextField
                            .fillMaxWidth(),
                        isError = ingredienteSeleccionado == null
                    )

                    ExposedDropdownMenu(
                        expanded = expandedIngredientes,
                        onDismissRequest = { expandedIngredientes = false }
                    ) {
                        listarIngredientes?.forEach { ingrediente ->
                            DropdownMenuItem(
                                text = { Text(ingrediente.ingrediente) },
                                onClick = {
                                    ingredienteSeleccionado = ingrediente
                                    expandedIngredientes = false
                                }
                            )
                        }
                    }
                }

                if (ingredienteSeleccionado == null) {
                    Text(
                        text = "Seleccionar un ingrediente es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Cantidad
                TextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad") },
                    leadingIcon = {
                        Icon(Icons.Filled.LineWeight, contentDescription = "Cantidad")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    isError = cantidad.isBlank()
                )

                if (cantidad.isBlank()) {
                    Text(
                        text = "El objetivo es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        if (cantidad.isNotEmpty() && recetaSeleccionada != null && ingredienteSeleccionado != null) {
                            scope.launch {
                                val nuevaRecetaIngrediente = RecetaIngrediente(
                                    cantidad = cantidad.toLong(),
                                    receta = recetaSeleccionada!!.idReceta!!,
                                    ingrediente = ingredienteSeleccionado!!.idIngrediente!!
                                )
                                viewModel.guardarRecetaIngrediente(nuevaRecetaIngrediente)
                                cantidad = ""
                                recetaSeleccionada = null
                                ingredienteSeleccionado = null
                                // Mostrar el Toast de éxito
                                Toast.makeText(context, "Ingrediente Insertado en la Receta", Toast.LENGTH_SHORT)
                                    .show()
                                viewModel.listarRecetaIngredientes()
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp),
                    enabled = cantidad.isNotEmpty() && recetaSeleccionada != null && ingredienteSeleccionado != null
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Ingrediente a la Receta")
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

                if (recetaIngredientesFiltrados.isEmpty()) {
                    Text(text = "No hay ingredientes en rectas disponibles.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(recetaIngredientesFiltrados) { recetaIngredientes ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    val nombreReceta =
                                        listarReceta?.find { it.idReceta == recetaIngredientes.receta }?.receta
                                            ?: "Sin receta"
                                    Text(
                                        text = "Receta: $nombreReceta",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    val nombreIngrediente =
                                        listarIngredientes?.find { it.idIngrediente == recetaIngredientes.ingrediente }?.ingrediente
                                            ?: "Sin ingrediente"
                                    Text(
                                        text = "Ingrediente: $nombreIngrediente",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Button(
                                        onClick = {
                                            recetaIngredienteEliminar = recetaIngredientes
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
            text = { Text("¿Está seguro que desea eliminar este ingrediente de la receta?") },
            confirmButton = {
                TextButton(onClick = {
                    recetaIngredienteEliminar?.let {
                        viewModel.eliminarRecetaIngrediente(it.idRecetaIngrediente!!)
                        Toast.makeText(context, "Ingrediente eliminado de la receta", Toast.LENGTH_SHORT).show()
                        viewModel.listarRecetaIngredientes()
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