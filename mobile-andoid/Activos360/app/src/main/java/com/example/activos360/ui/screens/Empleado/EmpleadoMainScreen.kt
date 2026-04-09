package com.example.activos360.ui.screens.Empleado

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.activos360.ui.screens.Empleado.HomeEmpleado
import com.example.activos360.ui.components.BottomCustomBar
import com.example.activos360.ui.modals.AssetDetailModal
import com.example.activos360.core.util.QrParse
import com.example.activos360.ui.screens.Empleado.details.ConfirmarResguardoScreen
import com.example.activos360.ui.screens.Empleado.details.DevolverActivoScreen
import com.example.activos360.ui.screens.Empleado.details.DetallesActivoScreen
import com.example.activos360.ui.screens.Empleado.details.ReportarDanoScreen
import com.example.activos360.ui.screens.Login.ScreeanChangePassword
import com.example.activos360.core.auth.TokenManager
import kotlinx.coroutines.launch

@Composable
fun EmpleadoMainScreen(navControllerPrincipal: NavController) {
    // 1. Creamos un controlador para nav entre Scanner y Perfil
    val bottomNavController = rememberNavController()

    var showModal by remember { mutableStateOf(false) }
    var codigoEscaneado by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

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
                UserProfile(
                    onNavigateToChangePassword = {
                        bottomNavController.navigate("change_password")
                    },
                    onLogout = {
                        TokenManager.clear()
                        navControllerPrincipal.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable("detalles_activo/{id}") { backStackEntry ->
                val idStr = backStackEntry.arguments?.getString("id") ?: "0"

                DetallesActivoScreen(
                    activoId = idStr.toLong(),
                    onBack = { navControllerPrincipal.popBackStack() },
                    onResguardarClick = {
                        // Cuando le da a Resguardar, VIAJAMOS al checklist con el mismo ID
                        navControllerPrincipal.navigate("confirmar_resguardo/$idStr")
                    },
                    onReportarDanoClick = { id, etiqueta, nombre ->
                        navControllerPrincipal.navigate(
                            "reportar_dano/$id/${Uri.encode(etiqueta)}/${Uri.encode(nombre)}"
                        )
                    },
                    onDevolverActivoClick = { idActivo, etiqueta, nombre ->
                        navControllerPrincipal.navigate(
                            "devolver_activo/$idActivo/${Uri.encode(etiqueta)}/${Uri.encode(nombre)}"
                        )
                    }
                )
            }

// RUTA 2: Pantalla del Checklist
            composable("confirmar_resguardo/{id}") { backStackEntry ->
                val idStr = backStackEntry.arguments?.getString("id") ?: "0"

                ConfirmarResguardoScreen(

                    activoId = idStr.toLong(),
                    onBack = { navControllerPrincipal.popBackStack() },
                    onConfirmed = {
                        // ¡ÉXITO! El back ya cambió el estatus.
                        // Regresamos al empleado al Home principal y limpiamos la pila
                        navControllerPrincipal.navigate("scanner") {
                            popUpTo(navControllerPrincipal.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            }

            composable("change_password") {
                ScreeanChangePassword(
                    correoFromFirstLogin = TokenManager.getCorreoFromToken(),
                    onBackClick = { bottomNavController.popBackStack() },
                    onPasswordUpdated = { bottomNavController.popBackStack() }
                )
            }

            composable("reportar_dano/{activoId}/{etiqueta}/{nombre}") { backStackEntry ->
                val activoId = backStackEntry.arguments?.getString("activoId")?.toLongOrNull() ?: 0L
                val etiqueta = Uri.decode(backStackEntry.arguments?.getString("etiqueta").orEmpty())
                val nombre = Uri.decode(backStackEntry.arguments?.getString("nombre").orEmpty())
                ReportarDanoScreen(
                    activoId = activoId,
                    activoEtiqueta = etiqueta,
                    activoNombre = nombre,
                    onBack = { navControllerPrincipal.popBackStack() },
                    onReportarSuccess = {
                        navControllerPrincipal.navigate("scanner") {
                            popUpTo(navControllerPrincipal.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            }

            composable("devolver_activo/{activoId}/{etiqueta}/{nombre}") { backStackEntry ->
                val activoId = backStackEntry.arguments?.getString("activoId")?.toLongOrNull() ?: 0L
                val etiqueta = Uri.decode(backStackEntry.arguments?.getString("etiqueta").orEmpty())
                val nombre = Uri.decode(backStackEntry.arguments?.getString("nombre").orEmpty())
                DevolverActivoScreen(
                    activoId = activoId,
                    activoEtiqueta = etiqueta,
                    activoNombre = nombre,
                    onBack = { navControllerPrincipal.popBackStack() },
                    onDevolverSuccess = {
                        navControllerPrincipal.navigate("scanner") {
                            popUpTo(navControllerPrincipal.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            }

        }
    }

    if (showModal) {
        AssetDetailModal(
            idActivo = codigoEscaneado,
            onDismiss = { showModal = false }, // Si el usuario cierra el modal deslizando
            onVerDetallesClick = {
                showModal = false // Cerramos el modal
                scope.launch {
                    val id = QrParse.resolveActivoId(codigoEscaneado) ?: 0L
                    navControllerPrincipal.navigate("detalles_activo/$id")
                }
            }
        )
    }
}

@Preview(showSystemUi = true, name = "Vista Empleado Principal")
@Composable
fun EmpleadoMainScreenPreview() {
    // Simulamos el NavController que viene desde el MainActivity
    val navControllerFalso = rememberNavController()

    // Invocamos tu pantalla
    EmpleadoMainScreen(navControllerPrincipal = navControllerFalso)
}

