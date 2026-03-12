package com.example.activos360.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.activos360.ui.Screens.Login.LoginScreen

@Composable
fun QRScanner(modifier: Modifier = Modifier) {
    val scanAreaColor = Color(0xFFFAFAFE)
    val innerBorderColor = Color(0xFF8B93FF)

    Box(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(0.85f)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(Color.White, shape = RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
        // Quitamos el .border de aquí porque el Canvas de adentro lo tapa
    ) {
        // --- Capa 1: Fondo claro con el agujero ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val insetTop = 80.dp.toPx()
            val insetSide = 40.dp.toPx()
            val innerCornerRadius = 20.dp.toPx()

            val holePath = Path().apply {
                addRoundRect(
                    roundRect = RoundRect(
                        rect = Rect(
                            offset = Offset(insetSide, insetTop),
                            size = Size(size.width - 2 * insetSide, size.height - (insetTop + 40.dp.toPx()))
                        ),
                        cornerRadius = CornerRadius(innerCornerRadius, innerCornerRadius)
                    )
                )
            }

            drawRect(color = scanAreaColor)
            clipPath(holePath, clipOp = ClipOp.Difference) {
                // Área de recorte (transparente)
            }
        }

        // --- Capa 2: Dibujar líneas punteadas del visor ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val insetTop = 80.dp.toPx()
            val insetSide = 40.dp.toPx()
            val innerCornerRadius = 20.dp.toPx()

            drawRoundRect(
                color = innerBorderColor.copy(alpha = 0.6f),
                topLeft = Offset(insetSide, insetTop),
                size = Size(size.width - 2 * insetSide, size.height - (insetTop + 40.dp.toPx())),
                cornerRadius = CornerRadius(innerCornerRadius, innerCornerRadius),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
            )
        }

        // --- Capa 3: EL BORDE MORADO EXTERIOR (NUEVO) ---
        // Al estar al final, se dibuja sobre el fondo crema
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = innerBorderColor,
                size = size,
                cornerRadius = CornerRadius(32.dp.toPx()),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        // --- Capa 4: Botones ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = innerBorderColor.copy(alpha = 0.85f),
                modifier = Modifier.size(48.dp)
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Flash",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Surface(
                shape = CircleShape,
                color = innerBorderColor.copy(alpha = 0.85f),
                modifier = Modifier.size(48.dp)
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Manual",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun preqr(){
    QRScanner()
}

