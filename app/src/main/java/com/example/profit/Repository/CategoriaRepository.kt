package com.example.profit.Repository

import com.example.profit.Interfaces.RetrofitClient
import com.example.profit.Model.Categoria
import com.example.profit.Model.Objetivo

class CategoriaRepository {
    suspend fun listarCategoria(): List<Categoria>{
        return RetrofitClient.apiService.listarCategoria()
    }

    suspend fun obtenerCategoriaPorID(id: Long): Categoria {
        return RetrofitClient.apiService.obtenerCategoriaPorID(id)
    }

    suspend fun guardarCategoria(categoria: Categoria): Categoria {
        return RetrofitClient.apiService.guardarCategoria(categoria)
    }

    suspend fun eliminarCategoria(idCategoria: Long) {
        RetrofitClient.apiService.eliminarCategoria(idCategoria)
    }
}