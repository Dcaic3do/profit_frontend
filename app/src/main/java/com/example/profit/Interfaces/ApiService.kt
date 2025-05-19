package com.example.profit.Interfaces

import com.example.profit.Model.Categoria
import com.example.profit.Model.Ingrediente
import com.example.profit.Model.Objetivo
import com.example.profit.Model.Receta
import com.example.profit.Model.RecetaIngrediente
import com.example.profit.Model.Usuario
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ObjetivoInterface {
    // Objetivo
    @GET("/objetivo/listar")
    suspend fun listarObjetivo(): List<Objetivo>

    @GET("/objetivo/listar/{idObjetivo}")
    suspend fun obtenerObjetivoPorID(@Path("idObjetivo") id: Long): Objetivo

    @POST("/objetivo/guardar")
    suspend fun guardarObjetivo(@Body objetivo: Objetivo): Objetivo

    @DELETE("/objetivo/eliminar/{idObjetivo}")
    suspend fun eliminarObjetivo(@Path("idObjetivo") id: Long): Response<Void>

    //Categoria
    @GET("/categoria/listar")
    suspend fun listarCategoria(): List<Categoria>

    @GET("/categoria/listar/{idCategoria}")
    suspend fun obtenerCategoriaPorID(@Path("idCategoria") id: Long): Categoria

    @POST("/categoria/guardar")
    suspend fun guardarCategoria(@Body categoria: Categoria): Categoria

    @DELETE("/categoria/eliminar/{idCategoria}")
    suspend fun eliminarCategoria(@Path("idCategoria") id: Long): Response<Void>

    //Ingrediente
    @GET("/ingrediente/listar")
    suspend fun listarIngrediente(): List<Ingrediente>

    @GET("/ingrediente/listar/{idIngrediente}")
    suspend fun obtenerIngredientePorID(@Path("idIngrediente") id: Long): Ingrediente

    @POST("/ingrediente/guardar")
    suspend fun guardarIngrediente(@Body ingrediente: Ingrediente): Ingrediente

    @DELETE("/ingrediente/eliminar/{idIngrediente}")
    suspend fun eliminarIngrediente(@Path("idIngrediente") id: Long): Response<Void>

    //Usuario
    @GET("/usuario/listar")
    suspend fun listarUsuario(): List<Usuario>

    @GET("/usuario/listar/{idUsuario}")
    suspend fun obtenerUsuarioPorID(@Path("idUsuario") id: Long): Usuario

    @POST("/usuario/guardar")
    suspend fun guardarUsuario(@Body usuario: Usuario): Usuario

    @DELETE("/usuario/eliminar/{idUsuario}")
    suspend fun eliminarUsuario(@Path("idUsuario") id: Long): Response<Void>

    //Receta
    @GET("/receta/listar")
    suspend fun listarReceta(): List<Receta>

    @GET("/receta/listar/{idReceta}")
    suspend fun obtenerRecetaPorID(@Path("idReceta") id: Long): Receta

    @POST("/receta/guardar")
    suspend fun guardarReceta(@Body receta: Receta): Receta

    @DELETE("/receta/eliminar/{idReceta}")
    suspend fun eliminarReceta(@Path("idReceta") id: Long): Response<Void>

    //Receta-Ingrediente
    @GET("/recetaIngrediente/listar")
    suspend fun listarRecetaIngrediente(): List<RecetaIngrediente>

    @GET("/recetaIngrediente/listar/{idRecetaIngrediente}")
    suspend fun obtenerRecetaIngredientePorID(@Path("idRecetaIngrediente") id: Long): RecetaIngrediente

    @POST("/recetaIngrediente/guardar")
    suspend fun guardarRecetaIngrediente(@Body recetaIngrediente: RecetaIngrediente): RecetaIngrediente

    @DELETE("/recetaIngrediente/eliminar/{idRecetaIngrediente}")
    suspend fun eliminarRecetaIngrediente(@Path("idRecetaIngrediente") id: Long): Response<Void>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080"

    val apiService: ObjetivoInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ObjetivoInterface::class.java)
    }
}

