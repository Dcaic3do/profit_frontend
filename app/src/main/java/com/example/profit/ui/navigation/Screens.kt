package com.example.profit.ui.navigation


sealed class Screens(val route: String) {
    object MenuPrincipal : Screens("menu_principal")
    object Categoria : Screens("categoria")
    object Ingrediente : Screens("ingrediente")
    object Usuario : Screens("usuario")
    object Objetivo : Screens("objetivo")
    object Receta : Screens("recetas")
    object AgregarReceta : Screens("agregar_receta")
}