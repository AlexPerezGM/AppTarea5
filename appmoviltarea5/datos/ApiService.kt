package com.example.appmoviltarea5.datos

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("users")
    suspend fun obtenerUsuarios(): List<Usuario>

    @GET("posts")
    suspend fun obtenerPublicaciones(@Query("userId") idUsuario: Int): List<Publicacion>
}

object Red {
    val api: ApiService = Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/").addConverterFactory(
        GsonConverterFactory.create()).build().create(ApiService::class.java)
}