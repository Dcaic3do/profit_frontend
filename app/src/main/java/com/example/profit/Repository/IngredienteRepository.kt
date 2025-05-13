package com.example.profit.Repository

import com.example.profit.Interfaces.RetrofitClient
import com.example.profit.Model.Categoria
import com.example.profit.Model.Ingrediente

class IngredienteRepository {
    suspend fun listarIngrediente(): List<Ingrediente>{
        return RetrofitClient.apiService.listarIngrediente()
    }

    suspend fun obtenerIngredientePorID(id: Long): Ingrediente {
        return RetrofitClient.apiService.obtenerIngredientePorID(id)
    }

    suspend fun guardarIngrediente(ingrediente: Ingrediente): Ingrediente {
        return RetrofitClient.apiService.guardarIngrediente(ingrediente)
    }

    suspend fun eliminarIngrediente(idIngrediente: Long) {
        RetrofitClient.apiService.eliminarIngrediente(idIngrediente)
    }
}