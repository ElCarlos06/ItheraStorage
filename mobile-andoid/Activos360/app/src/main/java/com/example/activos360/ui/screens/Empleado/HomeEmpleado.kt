package com.example.activos360.ui.screens.Empleado

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.components.Canvas2
import com.example.activos360.ui.components.QRScanner
import com.example.activos360.ui.components.ResguardosBanner
import com.example.activos360.ui.modals.ResguardosModal
import com.example.activos360.ui.viewmodel.EmpleadoViewModel
import com.example.activos360.ui.viewmodel.ResguardosBannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEmpleado(
    empleadoViewModel: EmpleadoViewModel = viewModel(),
    bannerViewModel: ResguardosBannerViewModel = viewModel()
) {
    val bannerState = bannerViewModel.state
    var showResguardosModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        bannerViewModel.cargar()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas2()

            ResguardosBanner(
                cantidad = bannerState.count,
                onClick = { showResguardosModal = true }
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                QRScanner()
            }

        }
    }

    if (showResguardosModal) {
        ResguardosModal(
            items = bannerState.items,
            onDismiss = { showResguardosModal = false }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun homePreview() {
    HomeEmpleado()
}
