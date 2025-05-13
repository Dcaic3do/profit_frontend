package com.example.profit.Model

import com.google.gson.annotations.SerializedName

data class Objetivo(
    @SerializedName("id_objetivo")
    val idObjetivo: Long? = null,
    val objetivo: String
)
