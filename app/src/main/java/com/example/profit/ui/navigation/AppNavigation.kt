package com.example.profit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.profit.Screen.CategoriaScreen
import com.example.profit.Screen.IngredienteScreen
import com.example.profit.Screen.MenuPrincipalScreen
import com.example.profit.Screen.UsuarioScreen
import com.example.profit.Screen.ObjetivoScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screens.MenuPrincipal.route) {
        composable(Screens.MenuPrincipal.route) {
            MenuPrincipalScreen(navController)
        }
        composable(Screens.Categoria.route) {
            CategoriaScreen(navController)
        }
        composable(Screens.Ingrediente.route) {
            IngredienteScreen(navController)
        }
        composable(Screens.Usuario.route) {
            UsuarioScreen(navController)
        }
        composable(Screens.Objetivo.route) {
            ObjetivoScreen(navController)
        }
    }
}

