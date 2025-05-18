package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import com.example.profit.Model.Objetivo
import com.example.profit.Model.Usuario
import com.example.profit.ViewModel.ObjetivoViewModel
import com.example.profit.ViewModel.UsuarioViewModel
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch

@Composable
fun UsuarioScreen(navController: NavHostController, viewModel: UsuarioViewModel = viewModel())  {
    val usuarios by viewModel.usuarios.observeAsState(emptyList())
    var usuario by remember { mutableStateOf("") }
    var correoElectronico by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var caloriaDiarias by remember { mutableStateOf("") }
    var codigoBusqueda by remember { mutableStateOf("") }

    //Lista Desplegable
    val objetivoViewModel: ObjetivoViewModel = viewModel()
    val listarObjetivos by objetivoViewModel.objetivos.observeAsState(emptyList())
    var objetivoSeleccionado by remember { mutableStateOf<Objetivo?>(null) }
    var expanded by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var usuarioEliminar by remember { mutableStateOf<Usuario?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Para el contexto del Toast

    // Cargar objetivos al iniciar
    LaunchedEffect(true) {
        viewModel.listarUsuarios()
        objetivoViewModel.listarObjetivos()
    }

    val usuariosFiltrados = usuarios?.filter {
        it.idUsuario?.toString()?.contains(codigoBusqueda, ignoreCase = true) ?: false
    } ?: emptyList()

    Column(modifier = Modifier.padding(top = 8.dp)) {

        Button(onClick = { navController.navigate(Screens.MenuPrincipal.route)}, modifier = Modifier.padding(top = 16.dp) ) {
            Text("Volver al menú principal")
        }

        Text(
            text = "Agregar Nuevo Usuario",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Nombre
        TextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = "Nombre Icon")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = usuario.isBlank() // Validación de nombre obligatorio
        )

        // Mensaje de error si el nombre está vacío
        if (usuario.isBlank()) {
            Text(
                text = "El nombre es obligatorio",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        //Correo Electronico
        TextField(
            value = correoElectronico,
            onValueChange = { correoElectronico = it },
            label = { Text("Correo Electrónico") },
            leadingIcon = {
                Icon(Icons.Filled.Email, contentDescription = "Email Icon")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = correoElectronico.isBlank() // Validación de nombre obligatorio
        )

        // Mensaje de error si el correo electronico está vacío
        if (correoElectronico.isBlank()) {
            Text(
                text = "El correo electrónico es obligatorio",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Contrasena
        TextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = "Nombre Icon")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = contrasena.isBlank() // Validación de nombre obligatorio
        )

        // Mensaje de error si la contraseña está vacía
        if (contrasena.isBlank()) {
            Text(
                text = "La contraseña es obligatoria",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Lista Desplegable Objetivos
        TextField(
            value = objetivoSeleccionado?.objetivo ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Seleccionar objetivo") },
            leadingIcon = {
                Icon(Icons.Filled.Star, contentDescription = "Nombre Icon")
            },
            trailingIcon = {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Expandir menú",
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = contrasena.isBlank() // Validación de nombre obligatorio
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listarObjetivos.forEach { objetivo ->
                DropdownMenuItem(
                    onClick = {
                        objetivoSeleccionado = objetivo
                        expanded = false
                    },
                    text = {Text(objetivo.objetivo)}
                )
            }
        }
        
        if (objetivoSeleccionado == null) {
            Text(
                text = "Seleccionar un objetivo es obligatorio",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Calorias Diarias
        TextField(
            value = caloriaDiarias,
            onValueChange = { caloriaDiarias = it },
            label = { Text("Calorias Diarías") },
            leadingIcon = {
                Icon(Icons.Filled.Favorite, contentDescription = "Objetivo Diario Icon")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = caloriaDiarias.isBlank()
        )

        if (caloriaDiarias.isBlank()) {
            Text(
                text = "Las calorias diarias son obligatorias",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                if (usuario.isNotEmpty() && correoElectronico.isNotEmpty() && contrasena.isNotEmpty() && objetivoSeleccionado != null && caloriaDiarias.isNotEmpty()) {
                    scope.launch {
                        val nuevoUsuario = Usuario(usuario = usuario, correoElectronico = correoElectronico, contrasena = contrasena, objetivo = objetivoSeleccionado!!.idObjetivo!!, caloriaDiarias = caloriaDiarias.toLong())
                        viewModel.guardarUsuario(nuevoUsuario)
                        usuario = ""
                        correoElectronico = ""
                        contrasena = ""
                        caloriaDiarias = ""
                        objetivoSeleccionado = null
                        // Mostrar el Toast de éxito
                        Toast.makeText(context, "Usuario Insertado", Toast.LENGTH_SHORT).show()
                        viewModel.listarUsuarios()
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            enabled = usuario.isNotBlank() && correoElectronico.isNotBlank() && contrasena.isNotBlank() && objetivoSeleccionado != null // Habilitar solo si todo es válido
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar Objetivo Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Agregar Objetivo")
        }

        Text(text = "Lista de Usuarios", style = MaterialTheme.typography.headlineSmall)
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

        if (usuariosFiltrados.isEmpty()) {
            Text(text = "No hay usuarios disponibles.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(usuariosFiltrados) { usuarios ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Código: ${usuarios.idUsuario}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Nombre: ${usuarios.usuario}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Correo Electrónico: ${usuarios.correoElectronico}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            val nombreObjetivoUsuario = listarObjetivos.find { it.idObjetivo == usuarios.objetivo }?.objetivo ?: "Sin objetivo"
                            Text(
                                text = "Objetivo: $nombreObjetivoUsuario",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Calorias Diarias: ${usuarios.caloriaDiarias}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Botón de Eliminar Objetivo con ícono
                            Button(
                                onClick = {
                                    // Mostrar el diálogo de confirmación de eliminación
                                    usuarioEliminar = usuarios
                                    showDialog = true
                                },
                                modifier = Modifier.padding(top = 8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Eliminar Usuario Icon"
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
            text = { Text("¿Está seguro que desea eliminar este objetivo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Eliminar el objetivo si el usuario confirma
                        usuarioEliminar?.let {
                            viewModel.eliminarUsuarios(it.idUsuario!!)
                            // Mostrar el Toast de éxito
                            Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                            viewModel.listarUsuarios()
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


