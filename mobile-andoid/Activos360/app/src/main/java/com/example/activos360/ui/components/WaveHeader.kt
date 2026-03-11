package com.example.activos360.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

// --- COMPONENTE DE LA OLA  HAY Q REUTILIZARLO PARA LAS DEMAS VISTAS---
@Composable
fun WaveHeader(color: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp) // Altura de la cabecera azul
    ) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, 0f) // Empezamos en la esquina superior izquierda
            lineTo(0f, height * 0.8f) // Bajamos por el borde izquierdo


            // Aquí creamos la curva de la ola
            cubicTo(
                x1 = width * 0.3f, y1 = height * 0.6f,  // Punto de control 1 (tira la curva hacia arriba)
                x2 = width * 0.7f, y2 = height * 1.1f,  // Punto de control 2 (tira la curva hacia abajo)
                x3 = width, y3 = height * 0.8f          // Punto final en el borde derecho
            )

            lineTo(width, 0f) // Sube al borde superior derecho
            close()
        }

        drawPath(path = path, color = color)
    }
}