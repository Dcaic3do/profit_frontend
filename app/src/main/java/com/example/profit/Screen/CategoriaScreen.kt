package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.profit.Model.Categoria
import com.example.profit.Model.Objetivo
import com.example.profit.ViewModel.CategoriaViewModel
import com.example.profit.ViewModel.ObjetivoViewModel
import com.example.profit.ui.components.BarraSuperior
import com.example.profit.ui.components.DrawerItem
import com.example.profit.ui.components.MenuLateral
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch

@Composable
fun CategoriaScreen(navController: NavHostController, viewModel: CategoriaViewModel = viewModel()) {
    val categorias by viewModel.categorias.observeAsState(emptyList())
    var categoria by remember { mutableStateOf("") }
    var codigoBusqueda by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var categoriaEliminar by remember { mutableStateOf<Categoria?>(null) }

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
        DrawerItem("Agregar Recetas", Screens.AgregarReceta.route, Icons.Default.Add)
    )

    val categoriasFiltradas = categorias?.filter {
        it.idCategoria?.toString()?.contains(codigoBusqueda, ignoreCase = true) ?: false
    } ?: emptyList()

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
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {

                Text(
                    text = "Agregar Nueva Categoría",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )

                TextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    leadingIcon = {
                        Icon(Icons.Filled.Category, contentDescription = "Categoría Icon")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    isError = categoria.isBlank()
                )

                if (categoria.isBlank()) {
                    Text(
                        text = "La categoría es obligatoria",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        if (categoria.isNotBlank()) {
                            scope.launch {
                                viewModel.guardarCategoria(Categoria(categoria = categoria))
                                categoria = ""
                                Toast.makeText(context, "Categoría insertada", Toast.LENGTH_SHORT).show()
                                viewModel.listarCategorias()
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    enabled = categoria.isNotBlank()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Categoría")
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

                if (categoriasFiltradas.isEmpty()) {
                    Text("No hay categorías disponibles.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(categoriasFiltradas) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Código: ${item.idCategoria}", style = MaterialTheme.typography.titleMedium)
                                    Text("Categoría: ${item.categoria}", style = MaterialTheme.typography.titleMedium)

                                    Button(
                                        onClick = {
                                            categoriaEliminar = item
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

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmación") },
                    text = { Text("¿Está seguro que desea eliminar esta categoría?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                categoriaEliminar?.let {
                                    viewModel.eliminarCategoria(it.idCategoria!!)
                                    Toast.makeText(context, "Categoría eliminada", Toast.LENGTH_SHORT).show()
                                    viewModel.listarCategorias()
                                }
                                showDialog = false
                            }
                        ) { Text("Sí") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}
