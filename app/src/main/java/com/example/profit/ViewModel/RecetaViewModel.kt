package com.example.profit.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profit.Model.Receta
import com.example.profit.Model.Usuario
import com.example.profit.Repository.RecetaRepository
import com.example.profit.Repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecetaViewModel : ViewModel() {
    private val repository = RecetaRepository()

    private val _recetas = MutableLiveData<List<Receta>?>(emptyList())
    val recetas: MutableLiveData<List<Receta>?> = _recetas

    fun listarRecetas() {
        viewModelScope.launch {
            val recetasList = withContext(Dispatchers.IO) {
                repository.listarReceta()
            }
            _recetas.postValue(recetasList ?: emptyList())
        }
    }

    fun obtenerRecetaPorID(id: Long) {
        viewModelScope.launch {
            val receta = withContext(Dispatchers.IO) {
                repository.obtenerRecetaPorID(id)
            }

        }
    }

    fun guardarReceta(receta: Receta) {
        viewModelScope.launch {
            repository.guardarReceta(receta)
            listarRecetas() // <-- Esto es clave para que la UI se actualice
        }
    }

    fun eliminarRecetas(id: Long) {
        viewModelScope.launch {
            repository.eliminarReceta(id)
            listarRecetas() // <-- Igualmente aquí
        }
    }
}