package com.example.activos360.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.Surface
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.Segment
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter

@Composable
fun FilaDetalleReporte(
    icono: ImageVector,
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = Color(0xFFF8F9FF),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF7B88FF),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = valor,
            fontSize = 14.sp,
            color = Color(0xFF636E72)
        )
    }
}

@Composable
fun FilaTipoFalla(
    icono: ImageVector = Icons.Outlined.Build,
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    color = Color(0xFFF8F9FF),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF7B88FF),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = valor,
            fontSize = 14.sp,
            color = Color(0xFF636E72),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FilaPrioridad(
    icono: ImageVector = Icons.Outlined.PriorityHigh,
    titulo: String = "Prioridad",
    prioridad: String = "ALTA"
) {
    val colorTexto = when (prioridad.uppercase()) {
        "ALTA" -> Color(0xFFD33030)
        "MEDIA" -> Color(0xFFFD9644)
        else -> Color(0xFF27AE60)
    }
    val colorFondo = when (prioridad.uppercase()) {
        "ALTA" -> Color(0xFFFFE9E9)
        "MEDIA" -> Color(0xFFFFF4EB)
        else -> Color(0xFFE8F8F0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    color = Color(0xFFF8F9FF),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF7B88FF),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436)
        )
        Spacer(modifier = Modifier.weight(1f))
        Surface(
            color = colorFondo,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = prioridad.uppercase(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                color = colorTexto,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun SeccionUsuarioReporte(
    fotoUrl: String? = null,
    nombreUsuario: String = "Usuario",
    etiquetaReporte: String = "Reportado por",
    fechaCompleta: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Foto de perfil
        Surface(
            modifier = Modifier
                .size(54.dp)
                .padding(top = 2.dp)
                .clip(RoundedCornerShape(14.dp)),
            color = Color(0xFFF1F2F6),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (!fotoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = fotoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(4.dp),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = etiquetaReporte,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = nombreUsuario,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF636E72)
                )
            }
            if (fechaCompleta.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = fechaCompleta,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFB2BEC3),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun SeccionDescripcion(
    titulo: String = "Descripción del problema",
    cuerpo: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color(0xFFF8F9FF),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Segment,
                    contentDescription = null,
                    tint = Color(0xFF7B88FF),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436)
            )
        }
        if (cuerpo.isNotBlank()) {
            Text(
                text = cuerpo,
                modifier = Modifier.padding(top = 16.dp, start = 2.dp),
                fontSize = 14.sp,
                color = Color(0xFF636E72),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun SeccionEvidencia(
    titulo: String = "Evidencia",
    imagenes: List<String> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color(0xFFF8F9FF),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Visibility,
                    contentDescription = null,
                    tint = Color(0xFF7B88FF),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (imagenes.isEmpty()) {
            Text(
                text = "Sin evidencias registradas",
                fontSize = 13.sp,
                color = Color(0xFFB2BEC3),
                modifier = Modifier.padding(start = 2.dp)
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                imagenes.take(3).forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF1F2F6))
                    )
                }
            }
        }
    }
}
