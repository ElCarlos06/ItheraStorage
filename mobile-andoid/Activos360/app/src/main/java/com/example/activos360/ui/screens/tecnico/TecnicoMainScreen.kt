package com.example.activos360.ui.screens.tecnico

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
import com.example.activos360.ui.screens.Empleado.CambiarContrasenaPerfilScreen
import com.example.activos360.ui.screens.Empleado.TecnicoHome
import com.example.activos360.ui.screens.Empleado.UserProfile
import com.example.activos360.ui.viewmodel.QrScanResult
import com.example.activos360.ui.viewmodel.QrScanViewModel
import kotlinx.coroutines.launch

@Composable
fun TecnicoMainScreen(
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
            is QrScanResult.NavegarDirecto -> {
                navControllerPrincipal.navigate("reporte_tecnico/${result.activoId}/${result.mantenimientoId}")
                qrScanViewModel.resetResult()
            }
            is QrScanResult.AbrirModal -> {
                codigoEscaneado = result.codigo
                showModal       = true
                qrScanViewModel.resetResult()
            }
            else -> Unit
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            if (currentRoute != "change_password") {
                BottomCustomBar(
                    navController = bottomNavController,
                    onQrScanned   = { codigo -> qrScanViewModel.procesarTecnico(codigo) }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController    = bottomNavController,
            startDestination = "scanner",
            modifier         = Modifier.padding(top = paddingValues.calculateTopPadding(), bottom = 0.dp)
        ) {
            composable("scanner") { TecnicoHome() }

            composable("perfil") {
                UserProfile(
                    onNavigateToChangePassword = { bottomNavController.navigate("change_password") },
                    onLogout = {
                        TokenManager.clear()
                        navControllerPrincipal.navigate("login") { popUpTo(0) }
                    }
                )
            }

            composable("change_password") {
                CambiarContrasenaPerfilScreen(
                    onBack    = { bottomNavController.popBackStack() },
                    onSuccess = { bottomNavController.popBackStack() }
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

@Preview(showSystemUi = true)
@Composable
fun TecnicoMainScreenPreview() {
    TecnicoMainScreen(navControllerPrincipal = rememberNavController())
}
