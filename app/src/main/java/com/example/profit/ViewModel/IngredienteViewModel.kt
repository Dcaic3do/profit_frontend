package com.example.profit.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profit.Model.Ingrediente
import com.example.profit.Model.Objetivo
import com.example.profit.Repository.IngredienteRepository
import com.example.profit.Repository.ObjetivoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngredienteViewModel : ViewModel() {
    private val repository = IngredienteRepository()

    private val _ingredientes = MutableLiveData<List<Ingrediente>?>(emptyList())
    val ingredientes: MutableLiveData<List<Ingrediente>?> = _ingredientes

    fun listarIngredientes() {
        viewModelScope.launch {
            val ingredientesList = withContext(Dispatchers.IO) {
                repository.listarIngrediente()
            }
            _ingredientes.postValue(ingredientesList)
        }
    }

    fun obtenerIngredientePorID(id: Long) {
        viewModelScope.launch {
            val ingrediente = withContext(Dispatchers.IO) {
                repository.obtenerIngredientePorID(id)
            }

        }
    }

    fun guardarIngrediente(ingrediente: Ingrediente) {
        viewModelScope.launch {
            repository.guardarIngrediente(ingrediente)
            listarIngredientes() // <-- Esto es clave para que la UI se actualice
        }
    }

    fun eliminarIngrediente(id: Long) {
        viewModelScope.launch {
            repository.eliminarIngrediente(id)
            listarIngredientes() // <-- Igualmente aquí
        }
    }
}