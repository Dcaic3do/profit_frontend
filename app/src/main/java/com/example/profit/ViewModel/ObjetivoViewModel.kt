package com.example.profit.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profit.Model.Objetivo
import com.example.profit.Repository.ObjetivoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ObjetivoViewModel : ViewModel() {
    private val repository = ObjetivoRepository()

    private val _objetivos = MutableLiveData<List<Objetivo>>(emptyList())
    val objetivos: MutableLiveData<List<Objetivo>> = _objetivos

    fun listarObjetivos() {
        viewModelScope.launch {
            val objetivosList = withContext(Dispatchers.IO) {
                repository.listarObjetivo()
            }
            _objetivos.postValue(objetivosList ?: emptyList())
        }
    }

    fun obtenerObjetivoPorID(id: Long) {
        viewModelScope.launch {
            val objetivo = withContext(Dispatchers.IO) {
                repository.obtenerObjetivoPorID(id)
            }

        }
    }

    fun guardarObjetivo(objetivo: Objetivo) {
        viewModelScope.launch {
            repository.guardarObjetivo(objetivo)
            listarObjetivos() // <-- Esto es clave para que la UI se actualice
        }
    }

    fun eliminarObjetivo(id: Long) {
        viewModelScope.launch {
            repository.eliminarObjetivo(id)
            listarObjetivos() // <-- Igualmente aquí
        }
    }
}
