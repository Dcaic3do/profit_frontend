package com.example.profit.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.profit.ui.components.BarraSuperior
import com.example.profit.ui.components.DrawerItem
import com.example.profit.ui.components.MenuLateral
import com.example.profit.ui.navigation.Screens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        DrawerItem("Usuarios", Screens.Usuario.route, Icons.Default.Person),
        DrawerItem("Objetivos", Screens.Objetivo.route, Icons.Default.FitnessCenter),
        DrawerItem("Ingredientes", Screens.Ingrediente.route, Icons.Default.Restaurant),
        DrawerItem("Categorías", Screens.Categoria.route, Icons.Default.Category)
    )

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
                    title = "Menú Principal",
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido a ProFit",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Selecciona una opción del menú lateral",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}




