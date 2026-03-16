package com.example.activos360.ui.screens.Empleado

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.activos360.ui.Screens.Empleado.HomeEmpleado
import com.example.activos360.ui.components.BottomCustomBar
import com.example.activos360.ui.screens.tecnico.UserProfile
import com.example.activos360.ui.modals.AssetDetailModal

@Composable
fun EmpleadoMainScreen(navControllerPrincipal: NavController) {
    // 1. Creamos un controlador para nav entre Scanner y Perfil
    val bottomNavController = rememberNavController()

    var showModal by remember { mutableStateOf(false) }
    var codigoEscaneado by remember { mutableStateOf("") }

    // 2. Scaffold
    Scaffold(
        bottomBar = {
            // Le pasamos el controlador para que los botones sepan a dónde ir
            BottomCustomBar(
                navController = bottomNavController,
                onQrScanned = { codigo ->
                    // Cuando la barra escanea, guardamos el código y mostramos el modal
                    codigoEscaneado = codigo
                    showModal = true
                }
            )
        }
    ) { paddingValues ->

        NavHost(
            navController = bottomNavController,
            startDestination = "scanner", // Cuando entre, que muestre el scanner primero
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding(),
                bottom = 0.dp)
        ) {

            // Ruta del Scanner
            composable("scanner") {
                HomeEmpleado()
            }

            // Ruta del Perfil
            composable("perfil") {
                UserProfile()
            }
        }
    }

    if (showModal) {
        AssetDetailModal(
            idActivo = codigoEscaneado,
            onDismiss = { showModal = false }, // Si el usuario cierra el modal deslizando
            onVerDetallesClick = {
                showModal = false // Cerramos el modal

                // NAVEGAMOS A LA VISTA COMPLETA PASANDO EL ID
                navControllerPrincipal.navigate("detalles_activo/$codigoEscaneado")
            }
        )
    }
}
