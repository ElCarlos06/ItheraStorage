package com.example.activos360.ui.models

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import com.example.activos360.ui.components.MoonIcon
import com.example.activos360.ui.components.MoonIcons
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResguardosModal(
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF8F9FF), // Gris muy claro de fondo
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Resguardos Pendientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "3 activos esperando confirmación",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Lista de tarjetas (Luego esto vendrá de una lista real de la BD)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                items(3) { // Repetimos 3 veces para el diseño
                    ResguardoItemCard(onConfirmClick)
                }
            }
        }
    }
}

@Composable
fun ResguardoItemCard(onConfirmClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row (verticalAlignment = Alignment.CenterVertically) {
                // Icono Laptop
                Surface(
                    color = Color(0xFFF1F2F6),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    MoonIcon(
                        icon = MoonIcons.DevicesMacbook,
                        contentDescription = null,
                        size = 24.dp,
                        tint = Color.Gray,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text("ACTIVO #0482", fontSize = 12.sp, color = Color.Gray)
                    Text("MacBook Pro 16\"", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge "EN PROCESO"
                Surface(
                    color = Color(0xFFFFF4E6),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "EN PROCESO",
                        color = Color(0xFFFD9644),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Botón Confirmar
                Button(
                    onClick = onConfirmClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B88FF)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text("Confirmar", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
@Preview
fun ResguardosModalPreview() {
    ResguardosModal(onDismiss = {}, onConfirmClick = {})
}