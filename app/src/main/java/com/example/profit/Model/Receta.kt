package com.example.profit.Model

import com.google.gson.annotations.SerializedName

data class Receta (
    @SerializedName("id_receta")
    val idReceta: Long? = null,
    val receta: String,
    val descripcion: String,
    val instrucciones: String,
    val tiempoTotal: Long,
    val calorias: Long,
    val proteinas: Long,
    val carbohidratos: Long,
    val grasas: Long,
    @SerializedName("id_categoria")
    val categoria: Long,
    @SerializedName("id_objetivo")
    val objetivo: Long
)