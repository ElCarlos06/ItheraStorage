package com.example.activos360.ui.screens.Empleado.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.Canvas2
import com.example.activos360.ui.components.CaracteristicasSeccion
import com.example.activos360.ui.components.HeaderRegresar
import com.example.activos360.ui.components.InfoCard
import com.example.activos360.ui.components.MainAssetCard
import com.example.activos360.ui.components.MoonIcons

@Composable
fun DetallesActivoScreen(
    onBack: () -> Unit = {},
    onResguardarClick: () -> Unit = {},
    onReportarDanoClick: () -> Unit = {}
) {
    val esMio = false

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                if (!esMio) {
                    Buttons(text = "Resguardar", onClick = onResguardarClick)
                } else {
                    Buttons(
                        text = "Devolver",
                        containerColor = Color(0xFF7B88FF).copy(alpha = 0.7f),
                        onClick = { }
                    )
                    Spacer(Modifier.height(12.dp))
                    Buttons(
                        text = "Reportar daño",
                        onClick = onReportarDanoClick
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Usamos el padding inferior del Scaffold para los botones
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header con botón de regreso
            HeaderRegresar(titulo = "Detalles del activo", onBackClick = onBack)

            // Contenedor de las tarjetas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp) // Espacio después de la ola
            ) {
                // 2. Tarjeta Principal (Laptop + ID)
                MainAssetCard(id = "ACTIVO #0482", nombre = "MacBook Pro 16\"")
                Spacer(modifier = Modifier.height(16.dp))

                // 3. Filas de Información usando TU COMPONENTE InfoCard
                InfoCard(
                    icon = MoonIcons.GenericBookmark,
                    label = "Marca",
                    value = "Apple"
                )

                InfoCard(
                    icon = MoonIcons.SoftwareSettings,
                    label = "Modelo",
                    value = "Pro 16"
                )

                // 4. Sección de Características
                // Aquí usamos un diseño similar pero expandido
                CaracteristicasSeccion(
                    lista = listOf("Característica 1", "Característica 2", "Característica 3")
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun preview() {
    DetallesActivoScreen()
}