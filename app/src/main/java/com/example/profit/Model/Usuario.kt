package com.example.profit.Model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id_usuario")
    val idUsuario: Long? = null,
    val usuario: String,
    @SerializedName("correo_electronico")
    val correoElectronico: String,
    val contrasena: String,
    val objetivo: Long,
    @SerializedName("caloria_diarias")
    val caloriaDiarias: Long
)
