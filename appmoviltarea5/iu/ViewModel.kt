package com.example.appmoviltarea5.iu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmoviltarea5.datos.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DirectorioViewModel(aplicacion: Application) : AndroidViewModel(aplicacion) {
    private val repositorio = Repositorio(Red.api, BaseFavoritos(aplicacion))

    private val _usuarios = MutableStateFlow<EstadoUi<List<Usuario>>>(EstadoUi.Cargando)
    val usuarios = _usuarios.asStateFlow()

    private val _publicaciones = MutableStateFlow<EstadoUi<List<Publicacion>>>(EstadoUi.Cargando)
    val publicaciones = _publicaciones.asStateFlow()

    private val _seleccionado = MutableStateFlow<Usuario?>(null)
    val seleccionado = _seleccionado.asStateFlow()

    val favoritos = repositorio.favoritos

    init {
        cargarUsuarios()
        viewModelScope.launch { repositorio.actualizarFavoritos() }
    }

    fun cargarUsuarios() = viewModelScope.launch {
        _usuarios.value = EstadoUi.Cargando
        _usuarios.value = repositorio.usuarios()
    }

    fun seleccionar(usuario: Usuario) {
        _seleccionado.value = usuario
        cargarPublicaciones()
    }

    fun cargarPublicaciones(){
        val usuario = _seleccionado.value ?: return
        viewModelScope.launch {
            _publicaciones.value = EstadoUi.Cargando
            _publicaciones.value = repositorio.publicaciones(usuario.id)
        }
    }

    fun alternarFavorito(usuario: Usuario) = viewModelScope.launch {
        val esFavorito = favoritos.value.any { it.id == usuario.id }
        repositorio.alternarFavorito(usuario, esFavorito)
    }
}