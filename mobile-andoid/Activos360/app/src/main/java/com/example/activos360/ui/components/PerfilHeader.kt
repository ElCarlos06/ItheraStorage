package com.example.activos360.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun PerfilHeader(viewModel: EmpleadoViewModel = viewModel(), nombre: String, rol: String) {
    val primaryColor = Color(0xFF7B88FF)

    Box(
        modifier = Modifier
            .fillMaxWidth(

            )
            .height(280.dp
            ) // Ajustamos la altura para que luzca como en el diseño
    ) {
        // 1. El fondo de la ola que ya tienes
        WaveHeader(color = primaryColor)

        // 2. Contenido Central
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 2.dp), // Espacio para no pegar la foto al borde superior
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            // FOTO DE PERFIL
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface (
                    modifier = Modifier.size(110.dp),
                    shape = CircleShape
                    ,
                    color = Color.White.copy(alpha = 0.3f), // Fondo por si no hay foto
                    border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.targeta), // Cambia por tu recurso
                        contentDescription = "Foto de perfil",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                // Botón de editar (el circulito blanco con el lápiz)
                Surface(
                    modifier = Modifier.size(30.dp).offset(x = (-5).dp, y = (-2).dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = primaryColor,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // NOMBRE
            Text(
                text = nombre,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ROL (El "Badge" semitransparente)
            Surface(
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = rol,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
@Preview
fun preheader() {
    PerfilHeader(nombre = "Carlos", rol = "TÉCNICO")
}