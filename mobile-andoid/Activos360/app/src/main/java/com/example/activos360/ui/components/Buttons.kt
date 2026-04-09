package com.example.activos360.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview

// ui/components/Buttons.kt
@Composable
fun Buttons(
    text: String,
    containerColor: Color = Color(0xFF8B93FF), // Color por defecto
    enabled: Boolean = true,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}




@Preview(showBackground = true, name = "Variantes de Botones")
@Composable
fun ButtonsPreview() {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre los botones
    ) {
        // 1. Botón por defecto (Azul/Morado)
        Buttons(
            text = "Finalizar mantenimiento",
            onClick = {}
        )

        // 2. Botón con color personalizado (Como el del Modal)
        Buttons(
            text = "Entendido",
            containerColor = Color(0xFF7B88FF),
            onClick = {}
        )

        // 3. Botón deshabilitado
        Buttons(
            text = "Guardar cambios",
            enabled = false,
            onClick = {}
        )
    }
}