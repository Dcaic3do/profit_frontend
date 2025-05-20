package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.profit.Model.Categoria
import com.example.profit.Model.Objetivo
import com.example.profit.Model.Receta
import com.example.profit.ViewModel.CategoriaViewModel
import com.example.profit.ViewModel.ObjetivoViewModel
import com.example.profit.ViewModel.RecetaViewModel
import com.example.profit.ui.components.BarraSuperior
import com.example.profit.ui.components.DrawerItem
import com.example.profit.ui.components.MenuLateral
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch

@Composable
fun RecetaScreen(navController: NavHostController, viewModel: RecetaViewModel = viewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val recetas by viewModel.recetas.observeAsState(emptyList())
    var codigoBusqueda by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var recetaEliminar by remember { mutableStateOf<Receta?>(null) }
    var selectedReceta by remember { mutableStateOf<Receta?>(null) }

    // Para el control de elementos expandidos
    val expandedItemIds = remember { mutableStateOf<Set<Long>>(emptySet()) }

    // Lista desplegable objetivo
    val objetivoViewModel: ObjetivoViewModel = viewModel()
    val listarObjetivos by objetivoViewModel.objetivos.observeAsState(emptyList())

    // Lista desplegable categoria
    val categoriaViewModel: CategoriaViewModel = viewModel()
    val listarCategorias by categoriaViewModel.categorias.observeAsState(emptyList())

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Cargar datos iniciales
    LaunchedEffect(true) {
        viewModel.listarRecetas()
        objetivoViewModel.listarObjetivos()
        categoriaViewModel.listarCategorias()
    }

    // Elementos del menú lateral
    val menuItems = listOf(
        DrawerItem("Usuarios", Screens.Usuario.route, Icons.Default.Person),
        DrawerItem("Objetivos", Screens.Objetivo.route, Icons.Default.FitnessCenter),
        DrawerItem("Ingredientes", Screens.Ingrediente.route, Icons.Default.Restaurant),
        DrawerItem("Categorías", Screens.Categoria.route, Icons.Default.Category),
        DrawerItem("Recetas", Screens.Receta.route, Icons.Default.RestaurantMenu),
        // DrawerItem("Agregar Recetas", Screens.AgregarReceta.route, Icons.Default.Add),
        DrawerItem("Ingredientes de Recetas", Screens.RecetaIngrediente.route, Icons.Default.AllInclusive)
    )

    // Filtrado mejorado para buscar tanto por ID como por nombre
    val recetasFiltrados = recetas?.filter {
        it.idReceta?.toString()?.contains(codigoBusqueda, ignoreCase = true) == true ||
                it.receta.contains(codigoBusqueda, ignoreCase = true)
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Receta")
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
                    text = "Lista de Recetas",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Campo de búsqueda redondeado
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

                if (recetasFiltrados != null) {
                    if (recetasFiltrados.isEmpty()) {
                        Text(text = "No hay recetas disponibles.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(recetasFiltrados) { receta ->
                                val isExpanded = expandedItemIds.value.contains(receta.idReceta ?: 0)
                                val rotationState by animateFloatAsState(
                                    targetValue = if (isExpanded) 180f else 0f,
                                    label = "rotation"
                                )

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        // Encabezado con nombre y botón de expandir
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    expandedItemIds.value = if (isExpanded) {
                                                        expandedItemIds.value - (receta.idReceta ?: 0)
                                                    } else {
                                                        expandedItemIds.value + (receta.idReceta ?: 0)
                                                    }
                                                },
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = receta.receta,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Expandir",
                                                modifier = Modifier.rotate(rotationState)
                                            )
                                        }

                                        // Contenido expandible
                                        AnimatedVisibility(visible = isExpanded) {
                                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                                Text(
                                                    text = "Código: ${receta.idReceta}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                Text(
                                                    text = "Descripción: ${receta.descripcion}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                Text(
                                                    text = "Instrucciones: ${receta.instrucciones}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                Text(
                                                    text = "Tiempo Total: ${receta.tiempoTotal} minutos",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                Text(
                                                    text = "Calorías: ${receta.calorias} kcal",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                Text(
                                                    text = "Proteínas: ${receta.proteinas}g",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                Text(
                                                    text = "Carbohidratos: ${receta.carbohidratos}g",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                Text(
                                                    text = "Grasas: ${receta.grasas}g",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                val nombreObjetivoReceta =
                                                    listarObjetivos.find { it.idObjetivo == receta.objetivo }?.objetivo
                                                        ?: "Sin objetivo"
                                                Text(
                                                    text = "Objetivo: $nombreObjetivoReceta",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                val nombreCategoriaReceta =
                                                    listarCategorias?.find { it.idCategoria == receta.categoria }?.categoria
                                                        ?: "Sin categoría"
                                                Text(
                                                    text = "Categoría: $nombreCategoriaReceta",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )

                                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                                // Fila de botones de acción
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceEvenly
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            selectedReceta = receta
                                                            showEditDialog = true
                                                        },
                                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Filled.Edit,
                                                            contentDescription = "Editar Receta"
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(text = "Editar")
                                                    }

                                                    Button(
                                                        onClick = {
                                                            recetaEliminar = receta
                                                            showDialog = true
                                                        },
                                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                                    ) {
                                                        Icon(
                                                            Icons.Filled.Delete,
                                                            contentDescription = "Eliminar Receta"
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
                        }
                    }
                }
            }
        }
    }

    // Diálogo para editar/añadir receta
    if (showAddDialog || (showEditDialog && selectedReceta != null)) {
        listarCategorias?.let {
            RecetaDialog(
                receta = if (showEditDialog) selectedReceta else null,
                objetivos = listarObjetivos,
                categorias = it,
                onDismiss = {
                    showAddDialog = false
                    showEditDialog = false
                },
                onSave = { recetaData ->
                    scope.launch {
                        viewModel.guardarReceta(recetaData)
                        viewModel.listarRecetas()
                        Toast.makeText(
                            context,
                            if (showEditDialog) "Receta actualizada correctamente" else "Receta agregada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    showAddDialog = false
                    showEditDialog = false
                }
            )
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDialog && recetaEliminar != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmación") },
            text = { Text("¿Está seguro que desea eliminar esta receta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        recetaEliminar?.idReceta?.let { id ->
                            viewModel.eliminarRecetas(id)
                            Toast.makeText(context, "Receta eliminada", Toast.LENGTH_SHORT).show()
                            viewModel.listarRecetas()
                        }
                        showDialog = false
                    }
                ) {
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RecetaDialog(
    receta: Receta? = null,
    objetivos: List<Objetivo>,
    categorias: List<Categoria>,
    onDismiss: () -> Unit,
    onSave: (Receta) -> Unit
) {
    var nombre by remember { mutableStateOf(receta?.receta ?: "") }
    var descripcion by remember { mutableStateOf(receta?.descripcion ?: "") }
    var instrucciones by remember { mutableStateOf(receta?.instrucciones ?: "") }
    var tiempoTotal by remember { mutableStateOf(receta?.tiempoTotal?.toString() ?: "") }
    var calorias by remember { mutableStateOf(receta?.calorias?.toString() ?: "") }
    var proteinas by remember { mutableStateOf(receta?.proteinas?.toString() ?: "") }
    var carbohidratos by remember { mutableStateOf(receta?.carbohidratos?.toString() ?: "") }
    var grasas by remember { mutableStateOf(receta?.grasas?.toString() ?: "") }

    var objetivoSeleccionado by remember {
        mutableStateOf(objetivos.find { it.idObjetivo == receta?.objetivo })
    }

    var categoriaSeleccionada by remember {
        mutableStateOf(categorias.find { it.idCategoria == receta?.categoria })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (receta == null) "Agregar Receta" else "Editar Receta") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                // Nombre de la Receta
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Receta") },
                    leadingIcon = { Icon(Icons.Filled.Restaurant, contentDescription = "Nombre Icon") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombre.isBlank()
                )

                if (nombre.isBlank()) {
                    Text(
                        text = "El nombre es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Descripción
                TextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    leadingIcon = { Icon(Icons.Filled.Description, contentDescription = "Description Icon") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = descripcion.isBlank()
                )

                if (descripcion.isBlank()) {
                    Text(
                        text = "La descripción es obligatoria",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Instrucciones
                TextField(
                    value = instrucciones,
                    onValueChange = { instrucciones = it },
                    label = { Text("Instrucciones") },
                    leadingIcon = { Icon(Icons.Filled.IntegrationInstructions, contentDescription = "Instrucciones Icon") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = instrucciones.isBlank()
                )

                if (instrucciones.isBlank()) {
                    Text(
                        text = "Las instrucciones son obligatorias",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tiempo Total
                TextField(
                    value = tiempoTotal,
                    onValueChange = { tiempoTotal = it },
                    label = { Text("Tiempo Total") },
                    leadingIcon = { Icon(Icons.Filled.Timer, contentDescription = "Time Icon") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = tiempoTotal.isBlank()
                )

                if (tiempoTotal.isBlank()) {
                    Text(
                        text = "El tiempo total es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Valores nutricionales
                TextField(
                    value = calorias,
                    onValueChange = { calorias = it },
                    label = { Text("Calorías") },
                    leadingIcon = { Icon(Icons.Filled.Favorite, contentDescription = "Calories Icon") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = calorias.isBlank()
                )

                if (calorias.isBlank()) {
                    Text(
                        text = "Las calorías son obligatorias",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = proteinas,
                    onValueChange = { proteinas = it },
                    label = { Text("Proteínas") },
                    leadingIcon = { Icon(Icons.Filled.CrueltyFree, contentDescription = "Protein Icon") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = proteinas.isBlank()
                )

                if (proteinas.isBlank()) {
                    Text(
                        text = "Las proteínas son obligatorias",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = carbohidratos,
                    onValueChange = { carbohidratos = it },
                    label = { Text("Carbohidratos") },
                    leadingIcon = { Icon(Icons.Filled.BakeryDining, contentDescription = "Carbohidrato Icon") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = carbohidratos.isBlank()
                )

                if (carbohidratos.isBlank()) {
                    Text(
                        text = "Los carbohidratos son obligatorios",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = grasas,
                    onValueChange = { grasas = it },
                    label = { Text("Grasas") },
                    leadingIcon = { Icon(Icons.Filled.Cake, contentDescription = "Fats Icon") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = grasas.isBlank()
                )

                if (grasas.isBlank()) {
                    Text(
                        text = "Las grasas son obligatorias",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Selector de objetivo
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
                        leadingIcon = { Icon(Icons.Filled.FitnessCenter, contentDescription = "Objetivo Icon") },
                        label = { Text("Seleccionar objetivo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedObjetivo) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = objetivoSeleccionado == null
                    )

                    ExposedDropdownMenu(
                        expanded = expandedObjetivo,
                        onDismissRequest = { expandedObjetivo = false }
                    ) {
                        objetivos.forEach { objetivo ->
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
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Selector de categoría
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
                        leadingIcon = { Icon(Icons.Filled.Category, contentDescription = "Category Icon") },
                        label = { Text("Seleccionar categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = categoriaSeleccionada == null
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false }
                    ) {
                        categorias.forEach { categoria ->
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
                        text = "Seleccionar una categoría es obligatorio",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nombre.isNotBlank() && descripcion.isNotBlank() && instrucciones.isNotBlank()
                        && tiempoTotal.isNotBlank() && calorias.isNotBlank() && proteinas.isNotBlank()
                        && carbohidratos.isNotBlank() && grasas.isNotBlank()
                        && objetivoSeleccionado != null && categoriaSeleccionada != null) {

                        val recetaData = Receta(
                            idReceta = receta?.idReceta,
                            receta = nombre,
                            descripcion = descripcion,
                            instrucciones = instrucciones,
                            tiempoTotal = tiempoTotal.toLongOrNull() ?: 0,
                            calorias = calorias.toLongOrNull() ?: 0,
                            proteinas = proteinas.toLongOrNull() ?: 0,
                            carbohidratos = carbohidratos.toLongOrNull() ?: 0,
                            grasas = grasas.toLongOrNull() ?: 0,
                            objetivo = objetivoSeleccionado!!.idObjetivo!!,
                            categoria = categoriaSeleccionada!!.idCategoria!!
                        )
                        onSave(recetaData)
                    }
                },
                enabled = nombre.isNotBlank() && descripcion.isNotBlank() && instrucciones.isNotBlank()
                        && tiempoTotal.isNotBlank() && calorias.isNotBlank() && proteinas.isNotBlank()
                        && carbohidratos.isNotBlank() && grasas.isNotBlank()
                        && objetivoSeleccionado != null && categoriaSeleccionada != null
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