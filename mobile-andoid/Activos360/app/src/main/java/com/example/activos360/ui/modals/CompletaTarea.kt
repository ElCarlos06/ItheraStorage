package com.example.activos360.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PowerOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalMantenimientoIncompleto(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            // La rayita gris de arriba
            Box(
                modifier = Modifier
                    .padding(vertical = 22.dp)
                    .width(45.dp)
                    .height(4.dp)
                    .background(Color(0xFFE0E0E0), CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Ilustración (El icono de los cables sueltos)
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color(0xFFFEF2F2), CircleShape), // Fondo rosado suave
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PowerOff, // O un Image si tienes el SVG
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFFE57373)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Título
            Text(
                text = "Mantenimiento incompleto",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC08484), // Color salmón oscuro
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Descripción
            Text(
                text = "Este mantenimiento esta incompleto, si te sales se guardará el progreso pero no podrás cerrar el mantenimiento hasta completarlo",
                fontSize = 15.sp,
                color = Color(0xFF2D3436),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Botón Entendido
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7B88FF)
                )
            ) {
                Text(
                    text = "Entendido",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}