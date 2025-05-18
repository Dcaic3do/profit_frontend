package com.example.profit.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.profit.Model.Objetivo
import com.example.profit.ViewModel.ObjetivoViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.navigation.NavHostController
import com.example.profit.ui.navigation.Screens

@Composable
fun ObjetivoScreen(navController: NavHostController, viewModel: ObjetivoViewModel = viewModel()) {
    val objetivos by viewModel.objetivos.observeAsState(emptyList())
    var objetivo by remember { mutableStateOf("") }
    var codigoBusqueda by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var objetivoEliminar by remember { mutableStateOf<Objetivo?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Para el contexto del Toast

    // Cargar objetivos al iniciar
    LaunchedEffect(true) {
        viewModel.listarObjetivos()
    }

    val objetivosFiltrados = objetivos?.filter {
        it.idObjetivo?.toString()?.contains(codigoBusqueda, ignoreCase = true) ?: false
    } ?: emptyList()

    Column(modifier = Modifier.padding(top = 60.dp)) {
        Button(onClick = { navController.navigate(Screens.MenuPrincipal.route) }) {
            Text("Volver al menú principal")
        }

        Text(text = "Lista de Objetivos", style = MaterialTheme.typography.headlineSmall)
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
                Icon(Icons.Filled.Person, contentDescription = "Nombre Icon")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = objetivo.isBlank() // Validación de nombre obligatorio
        )

        // Mensaje de error si el nombre está vacío
        if (objetivo.isBlank()) {
            Text(
                text = "El objetivo es obligatorio",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                if (objetivo.isNotEmpty()) {
                    scope.launch {
                        val nuevoObjetivo = Objetivo(objetivo = objetivo)
                        viewModel.guardarObjetivo(nuevoObjetivo)
                        objetivo = ""
                        // Mostrar el Toast de éxito
                        Toast.makeText(context, "objetivo insertado", Toast.LENGTH_SHORT).show()
                        viewModel.listarObjetivos()
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            enabled = objetivo.isNotEmpty() // Habilitar solo si todo es válido
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar Objetivo Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Agregar Objetivo")
        }

        if (objetivosFiltrados.isEmpty()) {
            Text(text = "No hay objetivos disponibles.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(objetivosFiltrados) { objetivos ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Código: ${objetivos.idObjetivo}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Objetivo: ${objetivos.objetivo}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Botón de Eliminar Objetivo con ícono
                            Button(
                                onClick = {
                                    // Mostrar el diálogo de confirmación de eliminación
                                    objetivoEliminar = objetivos
                                    showDialog = true
                                },
                                modifier = Modifier.padding(top = 8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Eliminar Objetivo Icon"
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
                        objetivoEliminar?.let {
                            viewModel.eliminarObjetivo(it.idObjetivo!!)
                            // Mostrar el Toast de éxito
                            Toast.makeText(context, "Objetivo eliminado", Toast.LENGTH_SHORT).show()
                            viewModel.listarObjetivos()
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