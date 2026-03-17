package com.example.activos360.ui.screens.Empleado

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.components.Canvas2
import com.example.activos360.ui.components.QRScanner
import com.example.activos360.ui.modals.AssetDetailModal
import com.example.activos360.ui.viewmodel.AssetDetailViewModel
import com.example.activos360.ui.viewmodel.EmpleadoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEmpleado(
    EmpleadoViewModel: EmpleadoViewModel = viewModel(),
    assetViewModel: AssetDetailViewModel = viewModel()
) {
    // Obtenemos el estado actual del modal
    val assetState = assetViewModel.uiState

    // 1. QUITAMOS EL SCAFFOLD y usamos un Box como contenedor principal
    Box(modifier = Modifier.fillMaxSize()) {

        // 2. Tu diseño original del escáner intacto
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas2()

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                QRScanner()
            }
        }

        // 3. La lógica de tu modal por encima de todo
        if (assetState.isVisible) {
            AssetDetailModal(
                idActivo = assetState.idEtiqueta,
                onDismiss = { assetViewModel.dismissModal() },
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