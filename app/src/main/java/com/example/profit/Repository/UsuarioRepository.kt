package com.example.profit.Repository

import com.example.profit.Interfaces.RetrofitClient
import com.example.profit.Model.Usuario

class UsuarioRepository {
    suspend fun listarUsuario(): List<Usuario>{
        return RetrofitClient.apiService.listarUsuario()
    }

    suspend fun obtenerUsuarioPorID(id: Long): Usuario {
        return RetrofitClient.apiService.obtenerUsuarioPorID(id)
    }

    suspend fun guardarUsuario(usuario: Usuario): Usuario {
        return RetrofitClient.apiService.guardarUsuario(usuario)
    }

    suspend fun eliminarUsuario(idUsuario: Long) {
        RetrofitClient.apiService.eliminarUsuario(idUsuario)
    }
}