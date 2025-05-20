package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CrueltyFree
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarIngredienteScreen(navController: NavHostController, viewModel: RecetaViewModel = viewModel()) {
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
        DrawerItem("Agregar Recetas", Screens.AgregarReceta.route, Icons.Default.Add),
        DrawerItem("Ingredientes de Recetas", Screens.RecetaIngrediente.route, Icons.Default.AllInclusive)
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
                    title = "Agregar Recetas",
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

                    Text(
                        text = "Agregar Nueva Receta",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    LazyColumn (modifier = Modifier.fillMaxWidth()){
                        item {
                            // Nombre de la Receta
                            TextField(
                                value = receta,
                                onValueChange = { receta = it },
                                label = { Text("Receta") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Restaurant, contentDescription = "Nombre Icon")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                isError = receta.isBlank() // Validación de nombre obligatorio
                            )

                            // Mensaje de error si el nombre está vacío
                            if (receta.isBlank()) {
                                Text(
                                    text = "El nombre es obligatorio",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        item {
                            //Descripcion
                            TextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Description, contentDescription = "Description Icon")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                isError = descripcion.isBlank() // Validación obligatoria
                            )

                            // Mensaje de error si el correo electronico está vacío
                            if (descripcion.isBlank()) {
                                Text(
                                    text = "La descripción es obligatoria",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        item {
                            // Instrucciones
                            TextField(
                                value = instrucciones,
                                onValueChange = { instrucciones = it },
                                label = { Text("Instrucciones") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.IntegrationInstructions,
                                        contentDescription = "Nombre Icon"
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                isError = instrucciones.isBlank() // Validación de nombre obligatorio
                            )

                            // Mensaje de error
                            if (instrucciones.isBlank()) {
                                Text(
                                    text = "Las instrucciones son obligatorias",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        item {
                            //Tiempo Total
                            TextField(
                                value = tiempoTotal,
                                onValueChange = { tiempoTotal = it },
                                label = { Text("Tiempo Total") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Timer, contentDescription = "Time Icon")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                isError = tiempoTotal.isBlank() // Validación obligatoria
                            )

                            // Mensaje de error
                            if (tiempoTotal.isBlank()) {
                                Text(
                                    text = "El tiempo total es obligatorio",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        item {
                            //Calorias
                            TextField(
                                value = calorias,
                                onValueChange = { calorias = it },
                                label = { Text("Calorias") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Favorite, contentDescription = "Calories Icon")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                isError = calorias.isBlank() // Validación obligatoria
                            )

                            // Mensaje de error
                            if (calorias.isBlank()) {
                                Text(
                                    text = "Las calorias son obligatorias",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        item {
                            //Proteinas
                            TextField(
                                value = proteinas,
                                onValueChange = { proteinas = it },
                                label = { Text("Proteinas") },
                                leadingIcon = {
                                    Icon(Icons.Filled.CrueltyFree, contentDescription = "Protein Icon")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                isError = proteinas.isBlank() // Validación obligatoria
                            )

                            // Mensaje de error
                            if (proteinas.isBlank()) {
                                Text(
                                    text = "Las proteinas son obligatorias",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                        item {
                            //Carbohidratos
                            TextField(
                                value = carbohidratos,
                                onValueChange = { carbohidratos = it },
                                label = { Text("Carbohidratos") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.BakeryDining,
                                        contentDescription = "Carbohidrate Icon"
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                isError = carbohidratos.isBlank() // Validación obligatoria
                            )

                            // Mensaje de error
                            if (carbohidratos.isBlank()) {
                                Text(
                                    text = "Los carbohidratos son obligatorios",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                        item {
                            //Grasas
                            TextField(
                                value = grasas,
                                onValueChange = { grasas = it },
                                label = { Text("Grasas") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Cake, contentDescription = "Fats Icon")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                isError = grasas.isBlank() // Validación obligatoria
                            )

                            // Mensaje de error
                            if (grasas.isBlank()) {
                                Text(
                                    text = "Las grasas son obligatorias",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        item {
                            // Lista Desplegable Objetivos
                            var expandedObjetivo by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expandedObjetivo,
                                onExpandedChange = { expandedObjetivo = !expandedObjetivo },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextField(
                                    value = objetivoSeleccionado?.objetivo ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    leadingIcon = {
                                        Icon(Icons.Filled.FitnessCenter, contentDescription = "Fats Icon")
                                    },
                                    label = { Text("Seleccionar objetivo") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedObjetivo)
                                    },
                                    modifier = Modifier
                                        .menuAnchor() // Este ancla el dropdown justo debajo del TextField
                                        .fillMaxWidth(),
                                    isError = objetivoSeleccionado == null
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedObjetivo,
                                    onDismissRequest = { expandedObjetivo = false }
                                ) {
                                    listarObjetivos.forEach { objetivo ->
                                        DropdownMenuItem(
                                            text = { Text(objetivo.objetivo) },
                                            onClick = {
                                                objetivoSeleccionado = objetivo
                                                expandedObjetivo = false
                                            }
                                        )
                                    }
                                }
                            }

                            if (objetivoSeleccionado == null) {
                                Text(
                                    text = "Seleccionar un objetivo es obligatorio",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        item {
                            // Lista Desplegable Categorias
                            var expandedCategoria by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = expandedCategoria,
                                onExpandedChange = { expandedCategoria = !expandedCategoria },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextField(
                                    value = categoriaSeleccionada?.categoria ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    leadingIcon = {
                                        Icon(Icons.Filled.Category, contentDescription = "Category Icon")
                                    },
                                    label = { Text("Seleccionar categoría") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    isError = categoriaSeleccionada == null
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedCategoria,
                                    onDismissRequest = { expandedCategoria = false }
                                ) {
                                    listarCategorias?.forEach { categoria ->
                                        DropdownMenuItem(
                                            text = { Text(categoria.categoria) },
                                            onClick = {
                                                categoriaSeleccionada = categoria
                                                expandedCategoria = false
                                            }
                                        )
                                    }
                                }
                            }

                            if (categoriaSeleccionada == null) {
                                Text(
                                    text = "Seleccionar una categoria es obligatorio",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    if (receta.isNotEmpty() && descripcion.isNotEmpty() && instrucciones.isNotEmpty() && tiempoTotal.isNotEmpty() && calorias.isNotEmpty() && proteinas.isNotEmpty() && carbohidratos.isNotEmpty() && grasas.isNotEmpty() && objetivoSeleccionado != null && categoriaSeleccionada != null) {
                                        scope.launch {
                                            val nuevaReceta = Receta(
                                                receta = receta,
                                                descripcion = descripcion,
                                                instrucciones = instrucciones,
                                                tiempoTotal = tiempoTotal.toLong(),
                                                calorias = calorias.toLong(),
                                                proteinas = proteinas.toLong(),
                                                carbohidratos = carbohidratos.toLong(),
                                                grasas = grasas.toLong(),
                                                objetivo = objetivoSeleccionado!!.idObjetivo!!,
                                                categoria = categoriaSeleccionada!!.idCategoria!!

                                            )
                                            viewModel.guardarReceta(nuevaReceta)
                                            receta = ""
                                            descripcion = ""
                                            instrucciones = ""
                                            tiempoTotal = ""
                                            calorias = ""
                                            proteinas = ""
                                            carbohidratos = ""
                                            grasas = ""
                                            objetivoSeleccionado = null
                                            categoriaSeleccionada = null
                                            // Mostrar el Toast de éxito
                                            Toast.makeText(context, "Receta Agregada", Toast.LENGTH_SHORT)
                                                .show()
                                            viewModel.listarRecetas()
                                        }
                                    }
                                },
                                modifier = Modifier.padding(top = 16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                enabled = receta.isNotEmpty() && descripcion.isNotEmpty() && instrucciones.isNotEmpty() && tiempoTotal.isNotEmpty() && calorias.isNotEmpty() && proteinas.isNotEmpty() && carbohidratos.isNotEmpty() && grasas.isNotEmpty() && objetivoSeleccionado != null && categoriaSeleccionada != null // Habilitar solo si todo es valido
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Agregar Objetivo Icon")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Agregar Objetivo")
                            }
                        }
                    }
                }
            }
        }
    }
}