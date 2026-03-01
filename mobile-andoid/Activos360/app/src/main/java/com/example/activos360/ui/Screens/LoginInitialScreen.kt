package com.example.activos360.ui.Screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.activos360.R
@Composable
fun LoginInitialScreen() {

    val primaryPurple = Color(0xFF7E78C8)
    val lightPurple = Color(0xFFB6BCE6)
    val backgroundColor = Color(0xFFF2F2F2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(top = 60.dp)
        ) {


            Icon(
                painter = painterResource(id = R.drawable.caja),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 1.dp)
            )

            // Título
            Text(
                text = "Control de activos\ninteligente",
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 1.dp, bottom = 10.dp),
                     textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Subtítulo
            Text(
                text = "Gestiona tus activos de forma fácil y rápida.\nEscanea, reporta y da seguimiento en un solo lugar",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 1.dp, bottom = 10.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Botón Cambiar contraseña
            Button(
                onClick = { },
                modifier = Modifier
                    .width(300.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryPurple,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Cambiar contraseña")
            }

            // Botón Iniciar sesión
            Button(
                onClick = { },
                modifier = Modifier
                    .width(300.dp)
                    .height(55.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = lightPurple,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Iniciar sesión")
            }
        }
    }
}