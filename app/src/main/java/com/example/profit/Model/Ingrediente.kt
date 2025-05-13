package com.example.profit.Model

import com.google.gson.annotations.SerializedName

data class Ingrediente(
    @SerializedName("id_ingrediente")
    val idIngrediente: Long? = null,
    val ingrediente: String,
    val unidad: String
)