package com.example.profit.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profit.Model.Receta
import com.example.profit.Model.RecetaIngrediente
import com.example.profit.Repository.RecetaIngredienteRepository
import com.example.profit.Repository.RecetaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecetaIngredienteViewModel : ViewModel() {
    private val repository = RecetaIngredienteRepository()

    private val _recetaIngredientes = MutableLiveData<List<RecetaIngrediente>?>(emptyList())
    val recetaIngredientes: MutableLiveData<List<RecetaIngrediente>?> = _recetaIngredientes

    fun listarRecetaIngredientes() {
        viewModelScope.launch {
            val recetaIngredientesList = withContext(Dispatchers.IO) {
                repository.listarRecetaIngrediente()
            }
            _recetaIngredientes.postValue(recetaIngredientesList)
        }
    }

    fun obtenerRecetaIngredientePorID(id: Long) {
        viewModelScope.launch {
            val recetaIngrediente = withContext(Dispatchers.IO) {
                repository.obtenerRecetaIngredientePorID(id)
            }

        }
    }

    fun guardarRecetaIngrediente(recetaIngrediente: RecetaIngrediente) {
        viewModelScope.launch {
            repository.guardarRecetaIngrediente(recetaIngrediente)
            listarRecetaIngredientes() // <-- Esto es clave para que la UI se actualice
        }
    }

    fun eliminarRecetaIngrediente(id: Long) {
        viewModelScope.launch {
            repository.eliminarRecetaIngrediente(id)
            listarRecetaIngredientes() // <-- Igualmente aquí
        }
    }
}