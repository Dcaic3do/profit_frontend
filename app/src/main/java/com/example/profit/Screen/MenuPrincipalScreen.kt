package com.example.profit.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.profit.ui.navigation.Screens

@Composable
fun MenuPrincipalScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Menú Principal")

        Button(onClick = { navController.navigate(Screens.Usuario.route) }) {
            Text("Gestión de Usuarios")
        }

        Button(onClick = { navController.navigate(Screens.Objetivo.route) }) {
            Text("Gestión de Objetivos")
        }

        Button(onClick = { navController.navigate(Screens.Ingrediente.route) }) {
            Text("Gestión de Ingredientes")
        }

        Button(onClick = { navController.navigate(Screens.Categoria.route) }) {
            Text("Gestión de Categorías")
        }
    }
}

