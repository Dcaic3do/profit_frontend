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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.profit.Model.Categoria
import com.example.profit.Model.Ingrediente
import com.example.profit.ViewModel.CategoriaViewModel
import com.example.profit.ViewModel.IngredienteViewModel
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch

@Composable
fun IngredienteScreen(navController: NavHostController, viewModel: IngredienteViewModel = viewModel()) {
    val ingredientes by viewModel.ingredientes.observeAsState(emptyList())
    var ingrediente by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var codigoBusqueda by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var ingredienteEliminar by remember { mutableStateOf<Ingrediente?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Para el contexto del Toast

    // Cargar Ingredientes al iniciar
    LaunchedEffect(true) {
        viewModel.listarIngredientes()
    }

    val ingredientesFiltrados = ingredientes?.filter {
        it.idIngrediente?.toString()?.contains(codigoBusqueda, ignoreCase = true) ?: false
    } ?: emptyList()

    Column(modifier = Modifier.padding(top = 60.dp)) {
        Button(onClick = { navController.navigate(Screens.MenuPrincipal.route) }) {
            Text("Volver al menú principal")
        }

        Text(text = "Lista de Ingredientes", style = MaterialTheme.typography.headlineSmall)
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
            text = "Agregar Nuevo Ingrediente",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        TextField(
            value = ingrediente,
            onValueChange = {ingrediente = it },
            label = { Text("Ingrediente") },
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = "Nombre Icon")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = ingrediente.isBlank() // Validación de nombre obligatorio
        )

        // Mensaje de error si el nombre está vacío
        if (ingrediente.isBlank()) {
            Text(
                text = "El ingrediente es obligatorio",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        TextField(
            value = unidad,
            onValueChange = {unidad = it },
            label = { Text("Unidad") },
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = "Nombre Icon")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            isError = unidad.isBlank() // Validación de nombre obligatorio
        )

        // Mensaje de error si el nombre está vacío
        if (unidad.isBlank()) {
            Text(
                text = "La unidad de medida es obligatoria",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                if (ingrediente.isNotEmpty() && unidad.isNotEmpty()) {
                    scope.launch {
                        val nuevoIngrediente = Ingrediente(ingrediente = ingrediente, unidad = unidad)
                        viewModel.guardarIngrediente(nuevoIngrediente)
                        ingrediente = ""
                        unidad = ""
                        // Mostrar el Toast de éxito
                        Toast.makeText(context, "Ingrediente insertado", Toast.LENGTH_SHORT).show()
                        viewModel.listarIngredientes()
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            enabled = ingrediente.isNotEmpty() // Habilitar solo si todo es válido
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar Ingrediente Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Agregar Ingrediente")
        }

        if (ingredientesFiltrados.isEmpty()) {
            Text(text = "No hay ingredientes disponibles.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ingredientesFiltrados) { ingredientes ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Código: ${ingredientes.idIngrediente}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Ingrediente: ${ingredientes.ingrediente}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Unidad: ${ingredientes.unidad}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Botón de Eliminar ingrediente con ícono
                            Button(
                                onClick = {
                                    // Mostrar el diálogo de confirmación de eliminación
                                    ingredienteEliminar = ingredientes
                                    showDialog = true
                                },
                                modifier = Modifier.padding(top = 8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Eliminar Ingrediente Icon"
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
            text = { Text("¿Está seguro que desea eliminar este ingrediente?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Eliminar el categoria si el usuario confirma
                        ingredienteEliminar?.let {
                            viewModel.eliminarIngrediente(it.idIngrediente!!)
                            // Mostrar el Toast de éxito
                            Toast.makeText(context, "Ingrediente eliminado", Toast.LENGTH_SHORT).show()
                            viewModel.listarIngredientes()
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