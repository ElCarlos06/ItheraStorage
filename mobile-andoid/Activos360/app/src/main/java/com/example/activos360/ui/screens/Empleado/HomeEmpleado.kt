package com.example.activos360.ui.Screens.Empleado

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.components.BottomCustomBar
import com.example.activos360.ui.components.Canvas2
import com.example.activos360.ui.components.QRScanner
import com.example.activos360.ui.components.ResguardosBanner
import com.example.activos360.ui.modals.AssetDetailModal // Asegúrate de importar tu modal
import com.example.activos360.ui.models.ResguardosModal
import com.example.activos360.ui.viewmodel.AssetDetailViewModel // El nuevo ViewModel del modal
import com.example.activos360.ui.viewmodel.EmpleadoViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEmpleado(
    empleadoViewModel: EmpleadoViewModel = viewModel(), // Corregido minúscula inicial
    assetViewModel: AssetDetailViewModel = viewModel()
) {
    // Estados de los ViewModels
    val assetState = assetViewModel.uiState
    val resguardosVisible = assetViewModel.isResguardosModalVisible

    Scaffold(
        bottomBar = {
            BottomCustomBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabecera con Ola
            Canvas2()

            // Barrita de pendientes (Banner)
            ResguardosBanner(
                cantidad = 3,
                onClick = { assetViewModel.showResguardos() } // Llamamos al VM
            )

            // Contenedor del Escáner
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                QRScanner()
            }
        }

        // 1. Modal de la lista de Resguardos (El de las mini tarjetas)
        if (resguardosVisible) {
            ResguardosModal(
                onDismiss = { assetViewModel.dismissResguardos() },
                onConfirmClick = { assetViewModel.confirmarResguardo() }
            )
        }

        // 2. Modal de Detalle (El que sale al escanear el QR)
        if (assetState.isVisible) {
            AssetDetailModal(
                nombreActivo = assetState.nombre,
                idActivo = assetState.idEtiqueta,
                onDismiss = { assetViewModel.dismissModal() },
                // AGREGAMOS ESTA LÍNEA:
                onVerDetallesClick = {
                    // Por ahora solo cerramos el modal, luego aquí pones la navegación
                    assetViewModel.dismissModal()
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun homePreview() {
    HomeEmpleado()
}