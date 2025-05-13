package com.example.profit.Repository

import com.example.profit.Interfaces.RetrofitClient
import com.example.profit.Model.Objetivo

class ObjetivoRepository {
    suspend fun listarObjetivo(): List<Objetivo>{
        return RetrofitClient.apiService.listarObjetivo()
    }

    suspend fun obtenerObjetivoPorID(id: Long): Objetivo {
        return RetrofitClient.apiService.obtenerObjetivoPorID(id)
    }

    suspend fun guardarObjetivo(objetivo: Objetivo): Objetivo {
        return RetrofitClient.apiService.guardarObjetivo(objetivo)
    }

    suspend fun eliminarObjetivo(idObjetivo: Long) {
        RetrofitClient.apiService.eliminarObjetivo(idObjetivo)
    }
}