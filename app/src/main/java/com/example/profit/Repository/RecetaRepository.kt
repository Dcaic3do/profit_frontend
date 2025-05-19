package com.example.profit.Repository

import com.example.profit.Interfaces.RetrofitClient
import com.example.profit.Model.Receta
import com.example.profit.Model.Usuario

class RecetaRepository {
    suspend fun listarReceta(): List<Receta>{
        return RetrofitClient.apiService.listarReceta()
    }

    suspend fun obtenerRecetaPorID(id: Long): Receta {
        return RetrofitClient.apiService.obtenerRecetaPorID(id)
    }

    suspend fun guardarReceta(receta: Receta): Receta {
        return RetrofitClient.apiService.guardarReceta(receta)
    }

    suspend fun eliminarReceta(idReceta: Long) {
        RetrofitClient.apiService.eliminarReceta(idReceta)
    }
}