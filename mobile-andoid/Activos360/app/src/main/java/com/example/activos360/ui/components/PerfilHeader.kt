package com.example.activos360.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.activos360.R
import com.example.activos360.ui.viewmodel.EmpleadoViewModel

@Composable
fun PerfilHeader(
    viewModel: EmpleadoViewModel = viewModel(),
    nombre: String,
    rol: String,
    onEditPhotoClick: () -> Unit = {}
) {
    val primaryColor = Color(0xFF7B88FF)
    val fotoUrl = viewModel.fotoUsuario
    val isUploading = viewModel.isUploadingPhoto

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        WaveHeader(color = primaryColor)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FOTO DE PERFIL
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.3f),
                    border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                ) {
                    if (isUploading) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                        }
                    } else if (!fotoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = fotoUrl,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            error = painterResource(R.drawable.targeta),
                            placeholder = painterResource(R.drawable.targeta)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.targeta),
                            contentDescription = "Foto por defecto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Botón de editar
                Surface(
                    modifier = Modifier
                        .size(30.dp)
                        .offset(x = (-5).dp, y = (-2).dp)
                        .clickable(enabled = !isUploading, onClick = onEditPhotoClick),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    MoonIcon(
                        icon = MoonIcons.GenericEdit,
                        contentDescription = "Editar foto",
                        tint = primaryColor,
                        size = 18.dp,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nombre,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

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
