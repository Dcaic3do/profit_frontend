package com.example.profit.Repository

import com.example.profit.Interfaces.RetrofitClient
import com.example.profit.Model.Receta
import com.example.profit.Model.RecetaIngrediente

class RecetaIngredienteRepository {
    suspend fun listarRecetaIngrediente(): List<RecetaIngrediente>{
        return RetrofitClient.apiService.listarRecetaIngrediente()
    }

    suspend fun obtenerRecetaIngredientePorID(id: Long): RecetaIngrediente {
        return RetrofitClient.apiService.obtenerRecetaIngredientePorID(id)
    }

    suspend fun guardarRecetaIngrediente(recetaIngrediente: RecetaIngrediente): RecetaIngrediente {
        return RetrofitClient.apiService.guardarRecetaIngrediente(recetaIngrediente)
    }

    suspend fun eliminarRecetaIngrediente(idRecetaIngrediente: Long) {
        RetrofitClient.apiService.eliminarRecetaIngrediente(idRecetaIngrediente)
    }
}