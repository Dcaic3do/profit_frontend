package com.example.profit.Model

import com.google.gson.annotations.SerializedName

data class Categoria(
    @SerializedName("id_categoria")
    val idCategoria: Long? = null,
    val categoria: String
)