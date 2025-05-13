package com.example.profit.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profit.Model.Categoria
import com.example.profit.Model.Objetivo
import com.example.profit.Repository.CategoriaRepository
import com.example.profit.Repository.ObjetivoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriaViewModel : ViewModel() {
    private val repository = CategoriaRepository()

    private val _categorias = MutableLiveData<List<Categoria>?>(emptyList())
    val categorias: MutableLiveData<List<Categoria>?> = _categorias

    fun listarCategorias() {
        viewModelScope.launch {
            val categoriasList = withContext(Dispatchers.IO) {
                repository.listarCategoria()
            }
            _categorias.postValue(categoriasList)
        }
    }

    fun obtenerCategoriaPorID(id: Long) {
        viewModelScope.launch {
            val categoria = withContext(Dispatchers.IO) {
                repository.obtenerCategoriaPorID(id)
            }

        }
    }

    fun guardarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            repository.guardarCategoria(categoria)
            listarCategorias() // <-- Esto es clave para que la UI se actualice
        }
    }

    fun eliminarCategoria(id: Long) {
        viewModelScope.launch {
            repository.eliminarCategoria(id)
            listarCategorias() // <-- Igualmente aquí
        }
    }
}