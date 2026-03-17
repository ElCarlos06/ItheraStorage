package com.example.activos360.ui.components

import android.R.attr.shape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
@Composable
fun ResguardosBanner(
    cantidad: Int,
    onClick: () -> Unit
) {
    // Si no hay pendientes, no dibujamos nada
    if (cantidad > 0) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(50), // Forma de píldora
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFF1F2F6)),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Punto naranja
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFD9644), CircleShape)
                )

                Text(
                    text = "$cantidad Resguardos Pendientes",
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3436)
                )

                MoonIcon(
                    icon = MoonIcons.ArrowsRight,
                    contentDescription = null,
                    tint = Color(0xFF7B88FF),
                    size = 18.dp
                )
            }
        }
    }
}


@Composable
@Preview
fun previewResguardosBanner(){
    ResguardosBanner(cantidad = 3, onClick = {})

}