package com.example.appmoviltarea5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.appmoviltarea5.ui.theme.AppMovilTarea5Theme
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmoviltarea5.iu.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMovilTarea5Theme {
                val navegador =  rememberNavController()
                val vm: DirectorioViewModel = viewModel()

                NavHost(navController = navegador, startDestination = "lista"){
                    composable("lista") {
                        ListaUsuarios(
                            vm,
                            alTocarUsuario = {vm.seleccionar(it); navegador.navigate("detalle")},
                            alIrAFavoritos = {navegador.navigate("favoritos")}
                        )
                    }
                    composable("detalle") {
                        PantallaDetalle(vm, alVolver = {navegador.popBackStack()})
                    }
                    composable("favoritos") {
                        PantallaFavoritos(
                          vm,
                            alTocarUsuario = {vm.seleccionar(it); navegador.navigate("detalle")},
                            alVolver = {navegador.popBackStack()}
                        )
                    }
                }
            }
        }
    }
}
