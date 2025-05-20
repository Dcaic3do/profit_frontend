package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.profit.Model.Objetivo
import com.example.profit.Model.Usuario
import com.example.profit.ViewModel.ObjetivoViewModel
import com.example.profit.ViewModel.UsuarioViewModel
import com.example.profit.ui.components.BarraSuperior
import com.example.profit.ui.components.DrawerItem
import com.example.profit.ui.components.MenuLateral
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun UsuarioScreen(navController: NavHostController, viewModel: UsuarioViewModel = viewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val usuarios by viewModel.usuarios.observeAsState(emptyList())
    var codigoBusqueda by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedUsuario by remember { mutableStateOf<Usuario?>(null) }
    var expandedItem by remember { mutableStateOf<Long?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // ViewModel para objetivos
    val objetivoViewModel: ObjetivoViewModel = viewModel()
    val objetivos by objetivoViewModel.objetivos.observeAsState(emptyList())

    // Cargar datos iniciales
    LaunchedEffect(true) {
        viewModel.listarUsuarios()
        objetivoViewModel.listarObjetivos()
    }

    // Elementos del menú lateral
    val menuItems = listOf(
        DrawerItem("Usuarios", Screens.Usuario.route, Icons.Default.Person),
        DrawerItem("Objetivos", Screens.Objetivo.route, Icons.Default.FitnessCenter),
        DrawerItem("Ingredientes", Screens.Ingrediente.route, Icons.Default.Restaurant),
        DrawerItem("Categorías", Screens.Categoria.route, Icons.Default.Category),
        DrawerItem("Recetas", Screens.Receta.route, Icons.Default.RestaurantMenu),
        DrawerItem("Agregar Recetas", Screens.AgregarReceta.route, Icons.Default.Add),
        DrawerItem("Ingredientes de Recetas", Screens.RecetaIngrediente.route, Icons.Default.AllInclusive)
    )

    // Filtrado de usuarios
    val usuariosFiltrados = usuarios?.filter { usuario ->
        usuario.idUsuario?.toString()?.contains(codigoBusqueda, ignoreCase = true) == true ||
                usuario.usuario.contains(codigoBusqueda, ignoreCase = true) ||
                usuario.correoElectronico.contains(codigoBusqueda, ignoreCase = true)
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
                    title = "Usuarios",
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Abrir menú",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Usuario")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Campo de búsqueda redondeado
                OutlinedTextField(
                    value = codigoBusqueda,
                    onValueChange = { codigoBusqueda = it },
                    label = { Text("Buscar usuario") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp)
                )

                // Lista de usuarios
                if (usuariosFiltrados!!.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay usuarios disponibles")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(usuariosFiltrados) { usuario ->
                            val isExpanded = expandedItem == usuario.idUsuario

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { expandedItem = if (isExpanded) null else usuario.idUsuario },
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Información básica siempre visible
                                    Text(
                                        text = usuario.usuario,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = "Correo: ${usuario.correoElectronico}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    // Información detallada y botones (visibles al expandir)
                                    AnimatedVisibility(visible = isExpanded) {
                                        Column {
                                            Spacer(modifier = Modifier.height(8.dp))

                                            val objetivo = objetivos.find { it.idObjetivo == usuario.objetivo }
                                            Text("ID: ${usuario.idUsuario}")
                                            Text("Objetivo: ${objetivo?.objetivo ?: "No asignado"}")
                                            Text("Calorías diarias: ${usuario.caloriaDiarias}")

                                            Spacer(modifier = Modifier.height(8.dp))

                                            // Botones de acción
                                            Row {
                                                Button(
                                                    onClick = {
                                                        selectedUsuario = usuario
                                                        showEditDialog = true
                                                    },
                                                    modifier = Modifier.padding(end = 8.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Edit,
                                                        contentDescription = "Editar"
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Editar")
                                                }

                                                Button(
                                                    onClick = {
                                                        selectedUsuario = usuario
                                                        showDeleteDialog = true
                                                    }
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Eliminar"
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
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

    // Diálogo de agregar usuario
    if (showAddDialog) {
        UsuarioDialog(
            onDismiss = { showAddDialog = false },
            onSave = { nuevoUsuario ->
                scope.launch {
                    viewModel.guardarUsuario(nuevoUsuario)
                    viewModel.listarUsuarios()
                    Toast.makeText(context, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
                }
                showAddDialog = false
            },
            objetivos = objetivos
        )
    }

    // Diálogo de editar usuario
    if (showEditDialog && selectedUsuario != null) {
        UsuarioDialog(
            usuario = selectedUsuario,
            onDismiss = { showEditDialog = false },
            onSave = { usuarioEditado ->
                scope.launch {
                    viewModel.guardarUsuario(usuarioEditado)
                    viewModel.listarUsuarios()
                    Toast.makeText(context, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                }
                showEditDialog = false
            },
            objetivos = objetivos
        )
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog && selectedUsuario != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro que desea eliminar el usuario ${selectedUsuario?.usuario}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedUsuario?.idUsuario?.let { id ->
                            scope.launch {
                                viewModel.eliminarUsuarios(id)
                                viewModel.listarUsuarios()
                                Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                            }
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
}

@Composable
fun UsuarioDialog(
    usuario: Usuario? = null,
    onDismiss: () -> Unit,
    onSave: (Usuario) -> Unit,
    objetivos: List<Objetivo>

) {
    var nombre by remember { mutableStateOf(usuario?.usuario ?: "") }
    var correo by remember { mutableStateOf(usuario?.correoElectronico ?: "") }
    var contrasena by remember { mutableStateOf(usuario?.contrasena ?: "") }
    var calorias by remember { mutableStateOf((usuario?.caloriaDiarias ?: "").toString()) }
    var objetivoSeleccionado by remember {
        mutableStateOf(objetivos.find { it.idObjetivo == usuario?.objetivo })
    }
    var expandedObjetivo by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (usuario == null) "Agregar Usuario" else "Editar Usuario") },
        text = {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                // Nombre de usuario
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de Usuario") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombre.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Correo electrónico
                TextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo Electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Correo") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = correo.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contraseña
                var passwordVisible by remember { mutableStateOf(false) }
                TextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible)
                                    "Ocultar contraseña"
                                else
                                    "Mostrar contraseña"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = contrasena.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de objetivo (versión estable)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = objetivoSeleccionado?.objetivo ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Objetivo") },
                        leadingIcon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Objetivo") },
                        trailingIcon = {
                            IconButton(onClick = { expandedObjetivo = !expandedObjetivo }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = objetivoSeleccionado == null
                    )

                    DropdownMenu(
                        expanded = expandedObjetivo,
                        onDismissRequest = { expandedObjetivo = false },
                        modifier = Modifier.width(with(LocalDensity.current) { 272.dp })
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

                Spacer(modifier = Modifier.height(16.dp))

                // Calorías diarias
                TextField(
                    value = calorias,
                    onValueChange = { calorias = it },
                    label = { Text("Calorías Diarias") },
                    leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = "Calorías") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = calorias.isBlank()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nombre.isNotBlank() && correo.isNotBlank() &&
                        contrasena.isNotBlank() && objetivoSeleccionado != null && calorias.isNotBlank()) {

                        val usuarioData = Usuario(
                            idUsuario = usuario?.idUsuario,
                            usuario = nombre,
                            correoElectronico = correo,
                            contrasena = contrasena,
                            objetivo = objetivoSeleccionado!!.idObjetivo!!,
                            caloriaDiarias = calorias.toLongOrNull() ?: 0
                        )
                        onSave(usuarioData)
                    }
                },
                enabled = nombre.isNotBlank() && correo.isNotBlank() &&
                        contrasena.isNotBlank() && objetivoSeleccionado != null && calorias.isNotBlank()
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