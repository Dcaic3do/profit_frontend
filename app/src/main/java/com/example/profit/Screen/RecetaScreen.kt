package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.profit.Model.Categoria
import com.example.profit.Model.Objetivo
import com.example.profit.Model.Receta
import com.example.profit.Model.Usuario
import com.example.profit.ViewModel.CategoriaViewModel
import com.example.profit.ViewModel.ObjetivoViewModel
import com.example.profit.ViewModel.RecetaViewModel
import com.example.profit.ViewModel.UsuarioViewModel
import com.example.profit.ui.components.BarraSuperior
import com.example.profit.ui.components.DrawerItem
import com.example.profit.ui.components.MenuLateral
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch

@Composable
fun RecetaScreen(navController: NavHostController, viewModel: RecetaViewModel = viewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val recetas by viewModel.recetas.observeAsState(emptyList())
    var receta by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var instrucciones by remember { mutableStateOf("") }
    var tiempoTotal by remember { mutableStateOf("") }
    var calorias by remember { mutableStateOf("") }
    var proteinas by remember { mutableStateOf("") }
    var carbohidratos by remember { mutableStateOf("") }
    var grasas by remember { mutableStateOf("") }
    var codigoBusqueda by remember { mutableStateOf("") }

    //Lista desplegable objetivo
    val objetivoViewModel: ObjetivoViewModel = viewModel()
    val listarObjetivos by objetivoViewModel.objetivos.observeAsState(emptyList())
    var objetivoSeleccionado by remember { mutableStateOf<Objetivo?>(null) }

    //Lista desplegable categoria
    val categoriaViewModel: CategoriaViewModel = viewModel()
    val listarCategorias by categoriaViewModel.categorias.observeAsState(emptyList())
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }

    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var recetaEliminar by remember { mutableStateOf<Receta?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Para el contexto del Toast

    // Cargar recetas al iniciar
    LaunchedEffect(true) {
        viewModel.listarRecetas()
        objetivoViewModel.listarObjetivos()
        categoriaViewModel.listarCategorias()
    }

    val menuItems = listOf(
        DrawerItem("Usuarios", Screens.Usuario.route, Icons.Default.Person),
        DrawerItem("Objetivos", Screens.Objetivo.route, Icons.Default.FitnessCenter),
        DrawerItem("Ingredientes", Screens.Ingrediente.route, Icons.Default.Restaurant),
        DrawerItem("Categorías", Screens.Categoria.route, Icons.Default.Category),
        DrawerItem("Recetas", Screens.Receta.route, Icons.Default.RestaurantMenu),
        DrawerItem("Agregar Recetas", Screens.AgregarReceta.route, Icons.Default.Add)
    )

    val recetasFiltrados = recetas?.filter {
        it.idReceta?.toString()?.contains(codigoBusqueda, ignoreCase = true) ?: false
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
                    title = "Recetas",
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
                Column(modifier = Modifier.padding(top = 8.dp)) {

                    Text(text = "Lista de Recetas", style = MaterialTheme.typography.headlineSmall)
                    // Campo para buscar por código
                    TextField(
                        value = codigoBusqueda,
                        onValueChange = { codigoBusqueda = it },
                        label = { Text("Buscar por código") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Codigo Icon")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )

                    if (recetasFiltrados.isEmpty()) {
                        Text(text = "No hay recetas disponibles.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(recetasFiltrados) { recetas ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Código: ${recetas.idReceta}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Receta: ${recetas.receta}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Descripción: ${recetas.descripcion}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Instrucciones: ${recetas.instrucciones}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Tiempo Total: ${recetas.tiempoTotal}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Calorias: ${recetas.calorias}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Proteinas: ${recetas.proteinas}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Carbohidratos: ${recetas.carbohidratos}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Grasas: ${recetas.grasas}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        val nombreObjetivoReceta =
                                            listarObjetivos.find { it.idObjetivo == recetas.objetivo }?.objetivo
                                                ?: "Sin objetivo"
                                        Text(
                                            text = "Objetivo: $nombreObjetivoReceta",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        val nombreCategoriaReceta =
                                            listarCategorias?.find { it.idCategoria == recetas.categoria }?.categoria
                                                ?: "Sin categoria"
                                        Text(
                                            text = "Categoria: $nombreCategoriaReceta",
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        // Botón de Eliminar Objetivo con ícono
                                        Button(
                                            onClick = {
                                                // Mostrar el diálogo de confirmación de eliminación
                                                recetaEliminar = recetas
                                                showDialog = true
                                            },
                                            modifier = Modifier.padding(top = 8.dp),
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            )
                                        ) {
                                            Icon(
                                                Icons.Filled.Delete,
                                                contentDescription = "Eliminar Receta Icon"
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Diálogo de confirmación de eliminación
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirmación") },
                        text = { Text("¿Está seguro que desea eliminar esta receta?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    // Eliminar el objetivo si el usuario confirma
                                    recetaEliminar?.let {
                                        viewModel.eliminarRecetas(it.idReceta!!)
                                        // Mostrar el Toast de éxito
                                        Toast.makeText(
                                            context,
                                            "Usuario eliminado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        viewModel.listarRecetas()
                                    }
                                    showDialog = false
                                }
                            ) {
                                Text("Sí")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDialog = false }
                            ) {
                                Text("No")
                            }
                        }
                    )
                }
            }
        }
    }
}