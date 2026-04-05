package com.example.activos360.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.R
import com.example.activos360.ui.viewmodel.EmpleadoViewModel

@Composable
fun Canvas2(viewModel: EmpleadoViewModel = viewModel())  {

    val primaryColor = Color(0xFF7B88FF)
    val fotoUsuario = viewModel.fotoUsuario
    val nombre = viewModel.nombreUsuario

    Box {

        WaveHeader(color = primaryColor)

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 60.dp)
        ) {

            Text(
                text = "Hola, \n$nombre",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Bienvenido de nuevo",
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 60.dp, end = 24.dp)
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {

            if (fotoUsuario != null) {

                // Cuando haya foto de BD
                Image(
                    painter = painterResource(R.drawable.targeta),
                    contentDescription = "Foto usuario",
                    modifier = Modifier.fillMaxSize()
                )

            }

            // Si es null, solo se verá el círculo vacío
        }
    }
}
