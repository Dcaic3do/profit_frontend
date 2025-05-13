package com.example.profit.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profit.Model.Objetivo
import com.example.profit.Model.Usuario
import com.example.profit.Repository.ObjetivoRepository
import com.example.profit.Repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioViewModel : ViewModel() {
    private val repository = UsuarioRepository()

    private val _usuarios = MutableLiveData<List<Usuario>?>(emptyList())
    val usuarios: MutableLiveData<List<Usuario>?> = _usuarios

    fun listarUsuarios() {
        viewModelScope.launch {
            val usuariosList = withContext(Dispatchers.IO) {
                repository.listarUsuario()
            }
            _usuarios.postValue(usuariosList)
        }
    }

    fun obtenerUsuarioPorID(id: Long) {
        viewModelScope.launch {
            val usuario = withContext(Dispatchers.IO) {
                repository.obtenerUsuarioPorID(id)
            }

        }
    }

    fun guardarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.guardarUsuario(usuario)
            listarUsuarios() // <-- Esto es clave para que la UI se actualice
        }
    }

    fun eliminarUsuarios(id: Long) {
        viewModelScope.launch {
            repository.eliminarUsuario(id)
            listarUsuarios() // <-- Igualmente aquí
        }
    }
}