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
import com.example.activos360.ui.modals.AssetDetailModal // Asegúrate de importar tu modal
import com.example.activos360.ui.viewmodel.AssetDetailViewModel // El nuevo ViewModel del modal
import com.example.activos360.ui.viewmodel.EmpleadoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEmpleado(
    EmpleadoViewModel: EmpleadoViewModel = viewModel(),
    assetViewModel: AssetDetailViewModel = viewModel()
) {
    // 2. Obtenemos el estado actual del modal
    val assetState = assetViewModel.uiState

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
            Canvas2()

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                QRScanner()
            }
        }
        //aqui el la logica del modal w
        if (assetState.isVisible) {
            AssetDetailModal(
                nombreActivo = assetState.nombre,
                idActivo = assetState.idEtiqueta,
                onDismiss = { assetViewModel.dismissModal() }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun homePreview() {
    HomeEmpleado()
}