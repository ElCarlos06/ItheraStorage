package com.example.activos360.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.R
import com.example.activos360.ui.viewmodel.EmpleadoViewModel
@Composable
fun HeaderRegresar(
    titulo: String,
    onBackClick: () -> Unit
) {
    val primaryColor = Color(0xFF7B88FF)

    Box(modifier = Modifier.fillMaxWidth()) {
        // Tu ola
        WaveHeader(color = primaryColor)

        // Contenido encima
        Column(
            modifier = Modifier
                .padding(start = 24.dp, top = 20.dp, end = 24.dp)
        ) {

            // --- BOTÓN ALARGADO (Cápsula) ---
            Surface(
                onClick = onBackClick,
                shape = RoundedCornerShape(50), // Forma de cápsula
                color = Color.White.copy(alpha = 0.3f), // Transparencia
                modifier = Modifier
                    .width(70.dp) // Lo hacemos más ancho
                    .height(45.dp) // Mantenemos la altura
            ) {
                // Centramos la flecha dentro de la cápsula
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp) // Tamaño estándar de icono
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título dinámico
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 38.sp
            )
        }
    }
}

@Composable
@Preview
fun nocw() {
    HeaderRegresar("Detalles del activo") { }
}