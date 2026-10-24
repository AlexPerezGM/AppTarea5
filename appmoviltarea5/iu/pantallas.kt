package com.example.appmoviltarea5.iu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appmoviltarea5.datos.*

@Composable
fun <T> VistaEstado(
    estado: EstadoUi<T>,
    alReintentar: () -> Unit,
    contenido: @Composable (T) -> Unit
) {
    when (estado) {
        EstadoUi.Cargando -> Box(Modifier.fillMaxSize(), Alignment.Center){
            CircularProgressIndicator()
        }
        is EstadoUi.Exito -> contenido(estado.datos)
        is EstadoUi.Error -> Mensaje(estado.mensaje, alReintentar)
        EstadoUi.SinInternet -> Mensaje("Sin conexion a Internet. Revisa tu red e intentalo de nuevo.",
            alReintentar)
    }
}

@Composable
private fun Mensaje(texto: String, alReintentar: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ){
        Text(texto)
        Spacer(Modifier.height(12.dp))
        Button(onClick = alReintentar) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun TarjetaUsuario(
    usuario: Usuario,
    esFavorito: Boolean,
    alTocar: () -> Unit,
    alAlternarFavorito: () -> Unit
){
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).clickable{alTocar()}
    ){
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically){
            Column(Modifier.weight(1f)){
                Text(usuario.nombre, fontWeight = FontWeight.Bold)
                Text(usuario.correo)
                Text(usuario.empresa.nombre)
            }
            TextButton(onClick = alAlternarFavorito) { Text(if (esFavorito) "★" else "☆") }
        }
    }
}
// Lista de usuarios
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaUsuarios(
    vm: DirectorioViewModel,
    alTocarUsuario: (Usuario) -> Unit,
    alIrAFavoritos: () -> Unit
){
    val estado by vm.usuarios.collectAsStateWithLifecycle()
    val favoritos by vm.favoritos.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        if (estado is EstadoUi.Error || estado is EstadoUi.SinInternet) vm.cargarUsuarios()
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Lista Usuarios")},
            actions = {
                TextButton(onClick = alIrAFavoritos) {Text("Favoritos")}}
        )
    }) { relleno ->
        Box(Modifier.padding(relleno)){
            VistaEstado(estado, alReintentar = vm::cargarUsuarios) {
                usuarios -> LazyColumn {
                    items(usuarios, key = { it.id }) {usuario ->
                        TarjetaUsuario(
                            usuario,
                            esFavorito = favoritos.any {it.id == usuario.id},
                            alTocar = {alTocarUsuario(usuario)},
                            alAlternarFavorito = { vm.alternarFavorito(usuario)}
                        )
                    }
                }
            }
        }
    }
}
// Detalles y publicaciones
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(vm: DirectorioViewModel, alVolver: () -> Unit) {
    val seleccionado by vm.seleccionado.collectAsStateWithLifecycle()
    val publicaciones by vm.publicaciones.collectAsStateWithLifecycle()
    val favoritos by vm.favoritos.collectAsStateWithLifecycle()
    val usuario = seleccionado ?: return

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(usuario.nombre) },
            navigationIcon = { TextButton(onClick = alVolver) { Text("←") } },
            actions = {
                TextButton(onClick = { vm.alternarFavorito(usuario) }) {
                    Text(if (favoritos.any { it.id == usuario.id }) "★ Quitar" else "☆ Favorito")
                }
            }
        )
    }) { relleno ->
        Column(Modifier.padding(relleno).padding(16.dp)) {
            Text("Correo: ${usuario.correo}")
            Text("Teléfono: ${usuario.telefono ?: "-"}")
            Text("Sitio web: ${usuario.sitioWeb ?: "-"}")
            Text("Empresa: ${usuario.empresa.nombre}")
            Spacer(Modifier.height(16.dp))
            Text("Publicaciones", fontWeight = FontWeight.Bold)
            Box(Modifier.weight(1f)) {
                VistaEstado(publicaciones, alReintentar = vm::cargarPublicaciones) { lista ->
                    LazyColumn {
                        items(lista, key = { it.id }) { publicacion ->
                            Column(Modifier.padding(vertical = 8.dp)) {
                                Text(publicacion.titulo, fontWeight = FontWeight.SemiBold)
                                Text(publicacion.cuerpo)
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

// Lista de favoritos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFavoritos(
    vm: DirectorioViewModel,
    alTocarUsuario: (Usuario) -> Unit,
    alVolver: () -> Unit
) {
    val favoritos by vm.favoritos.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Favoritos") },
            navigationIcon = { TextButton(onClick = alVolver) { Text("←") } }
        )
    }) { relleno ->
        Box(Modifier.padding(relleno)) {
            if (favoritos.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Aún no tienes favoritos.")
                }
            } else {
                LazyColumn {
                    items(favoritos, key = { it.id }) { favorito ->
                        val usuario = favorito.aUsuario()
                        TarjetaUsuario(
                            usuario,
                            esFavorito = true,
                            alTocar = { alTocarUsuario(usuario) },
                            alAlternarFavorito = { vm.alternarFavorito(usuario) }
                        )
                    }
                }
            }
        }
    }
}