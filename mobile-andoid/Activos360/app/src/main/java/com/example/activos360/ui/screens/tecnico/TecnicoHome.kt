package com.example.activos360.ui.Screens.Empleado

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.activos360.ui.viewmodel.EmpleadoViewModel
// 1. Borra el import de com.example.activos360.navigation.NavigationBar
// 2. Asegúrate de que importe el de com.example.activos360.ui.components.BottomCustomBar

@Composable
fun TecnicoHome(viewModel: EmpleadoViewModel = viewModel()) {
    Scaffold(
        bottomBar = {
            BottomCustomBar() // <-- Usamos el nombre nuevo
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
    }
}

@Composable
@Preview(showBackground = true)
fun tchome(){
    TecnicoHome()
}