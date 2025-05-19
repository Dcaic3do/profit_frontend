package com.example.profit.Model

import com.google.gson.annotations.SerializedName

data class RecetaIngrediente (
    @SerializedName("id_recetaIngrediente")
    val idRecetaIngrediente: Long? = null,
    @SerializedName("id_receta")
    val receta: Long,
    @SerializedName("id_ingrediente")
    val ingrediente: Long,
    val cantidad: Long
)