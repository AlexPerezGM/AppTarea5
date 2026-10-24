package com.example.appmoviltarea5.datos

import com.google.gson.annotations.SerializedName

data class Empresa (
    @SerializedName("name") val nombre: String
)

data class Usuario(
    val id: Int,
    @SerializedName("name") val nombre: String,
    @SerializedName("email") val correo: String,
    @SerializedName("phone") val telefono: String?,
    @SerializedName("website") val sitioWeb: String?,
    @SerializedName("company") val empresa: Empresa
)

data class Publicacion(
    val id: Int,
    @SerializedName("userId") val idUsuario: Int,
    @SerializedName("title") val titulo: String,
    @SerializedName("body") val cuerpo: String
)

sealed interface EstadoUi<out T> {
    data object Cargando : EstadoUi<Nothing>
    data class Exito<out T>(val datos: T) : EstadoUi<T>
    data class Error(val mensaje: String) : EstadoUi<Nothing>
    data object SinInternet : EstadoUi<Nothing>
}