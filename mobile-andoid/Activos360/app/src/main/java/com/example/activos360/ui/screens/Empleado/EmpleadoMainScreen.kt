package com.example.activos360.ui.screens.Empleado

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.util.QrParse
import com.example.activos360.ui.components.BottomCustomBar
import com.example.activos360.ui.modals.AssetDetailModal
import com.example.activos360.ui.screens.Empleado.details.ConfirmarResguardoScreen
import com.example.activos360.ui.screens.Empleado.details.DevolverActivoScreen
import com.example.activos360.ui.screens.Empleado.details.DetallesActivoScreen
import com.example.activos360.ui.screens.Empleado.details.ReportarDanoScreen
import com.example.activos360.ui.viewmodel.QrScanResult
import com.example.activos360.ui.viewmodel.QrScanViewModel
import kotlinx.coroutines.launch

@Composable
fun EmpleadoMainScreen(
    navControllerPrincipal: NavController,
    qrScanViewModel: QrScanViewModel = viewModel()
) {
    val bottomNavController = rememberNavController()
    val context             = LocalContext.current
    val qrState             = qrScanViewModel.uiState

    val currentRoute = bottomNavController.currentBackStackEntryAsState().value?.destination?.route

    var showModal       by remember { mutableStateOf(false) }
    var codigoEscaneado by remember { mutableStateOf("") }

    // Reaccionar al resultado del escaneo
    LaunchedEffect(qrState.result) {
        when (val result = qrState.result) {
            is QrScanResult.QrInvalido -> {
                Toast.makeText(context, "Este QR no es de un activo", Toast.LENGTH_SHORT).show()
                qrScanViewModel.resetResult()
            }
            is QrScanResult.ActivoDadoDeBaja -> {
                Toast.makeText(context, "Este activo está dado de baja", Toast.LENGTH_LONG).show()
                qrScanViewModel.resetResult()
            }
            is QrScanResult.AbrirModal -> {
                codigoEscaneado = result.codigo
                showModal       = true
                qrScanViewModel.resetResult()
            }
            else -> Unit // Idle — no hacer nada
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            if (currentRoute != "change_password") {
                BottomCustomBar(
                    navController = bottomNavController,
                    onQrScanned   = { codigo -> qrScanViewModel.procesarEmpleado(codigo) }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController    = bottomNavController,
            startDestination = "scanner",
            modifier         = Modifier.padding(top = paddingValues.calculateTopPadding(), bottom = 0.dp)
        ) {
            composable("scanner") { HomeEmpleado() }

            composable("perfil") {
                UserProfile(
                    onNavigateToChangePassword = { bottomNavController.navigate("change_password") },
                    onLogout = {
                        TokenManager.clear()
                        navControllerPrincipal.navigate("login") { popUpTo(0) }
                    }
                )
            }

            composable("detalles_activo/{id}") { backStackEntry ->
                val idStr = backStackEntry.arguments?.getString("id") ?: "0"
                DetallesActivoScreen(
                    activoId        = idStr.toLong(),
                    onBack          = { navControllerPrincipal.popBackStack() },
                    onResguardarClick = { navControllerPrincipal.navigate("confirmar_resguardo/$idStr") },
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

            composable("confirmar_resguardo/{id}") { backStackEntry ->
                val idStr = backStackEntry.arguments?.getString("id") ?: "0"
                ConfirmarResguardoScreen(
                    activoId    = idStr.toLong(),
                    onBack      = { navControllerPrincipal.popBackStack() },
                    onConfirmed = {
                        navControllerPrincipal.navigate("scanner") {
                            popUpTo(navControllerPrincipal.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    onReportarDano = {
                        navControllerPrincipal.navigate(
                            "reportar_dano/$idStr/${Uri.encode("Activo")}/${Uri.encode("Activo #$idStr")}"
                        )
                    }
                )
            }

            composable("change_password") {
                CambiarContrasenaPerfilScreen(
                    onBack    = { bottomNavController.popBackStack() },
                    onSuccess = { bottomNavController.popBackStack() }
                )
            }

            composable("reportar_dano/{activoId}/{etiqueta}/{nombre}") { backStackEntry ->
                val activoId = backStackEntry.arguments?.getString("activoId")?.toLongOrNull() ?: 0L
                val etiqueta = Uri.decode(backStackEntry.arguments?.getString("etiqueta").orEmpty())
                val nombre   = Uri.decode(backStackEntry.arguments?.getString("nombre").orEmpty())
                ReportarDanoScreen(
                    activoId       = activoId,
                    activoEtiqueta = etiqueta,
                    activoNombre   = nombre,
                    onBack         = { navControllerPrincipal.popBackStack() },
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
                val nombre   = Uri.decode(backStackEntry.arguments?.getString("nombre").orEmpty())
                DevolverActivoScreen(
                    activoId       = activoId,
                    activoEtiqueta = etiqueta,
                    activoNombre   = nombre,
                    onBack         = { navControllerPrincipal.popBackStack() },
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
            idActivo         = codigoEscaneado,
            onDismiss        = { showModal = false },
            onVerDetallesClick = {
                showModal = false
                kotlinx.coroutines.MainScope().launch {
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
    EmpleadoMainScreen(navControllerPrincipal = rememberNavController())
}
