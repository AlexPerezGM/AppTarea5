package com.example.appmoviltarea5.datos

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> llamadaSegura(bloque: suspend () -> T): EstadoUi<T> = try {
    EstadoUi.Exito(bloque())
} catch (e: CancellationException) { throw e
} catch (e: IOException) {
    EstadoUi.SinInternet
} catch (e: HttpException) {
    EstadoUi.Error("Error del servidor (${e.code()})")
} catch (e: Exception) {
    EstadoUi.Error("Ocurrio un error inesperado")
}

class Repositorio(
    private val api: ApiService,
    private val baseFavoritos: BaseFavoritos
) {
    suspend fun usuarios() = llamadaSegura {api.obtenerUsuarios()}
    suspend fun publicaciones(idUsuario: Int) = llamadaSegura { api.obtenerPublicaciones(idUsuario) }

    private val _favoritos = MutableStateFlow<List<UsuarioFavorito>>(emptyList())
    val favoritos: StateFlow<List<UsuarioFavorito>> = _favoritos.asStateFlow()

    suspend fun actualizarFavoritos() = withContext(Dispatchers.IO) {
        _favoritos.value = baseFavoritos.obtenerTodos()
    }

    suspend fun alternarFavorito(usuario: Usuario, esFavorito: Boolean) {
        withContext(Dispatchers.IO) {
            if (esFavorito) baseFavoritos.eliminar(usuario.id)
            else baseFavoritos.insertar(usuario.aFavorito())
        }
        actualizarFavoritos()
    }
}
