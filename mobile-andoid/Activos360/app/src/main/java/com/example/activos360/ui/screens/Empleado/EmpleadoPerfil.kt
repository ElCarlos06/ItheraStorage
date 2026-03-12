package com.example.activos360.ui.screens.Empleado

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Password
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
import com.example.activos360.ui.components.BottomCustomBar
import com.example.activos360.ui.components.PerfilHeader

@Composable
fun EmpleadoPerfil() {
    androidx.compose.material3.Scaffold(
        bottomBar = { BottomCustomBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FF))
        ) {
            // 1. HEADER (Ahora con menos altura interna)
            PerfilHeader()

            // 2. Título ESTADÍSTICAS (Ajustamos el padding superior a 0 o negativo)
            Text(
                text = "ESTADÍSTICAS",
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-10).dp), // <-- Subimos el texto un poco para pegarlo a la ola
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            // 3. SECCIÓN DE ESTADÍSTICAS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-5).dp), // <-- Subimos las tarjetas también
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta 1: Reparados
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF8B93FF).copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF8B93FF), modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("08", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Text("Mantenimientos reparados", fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
                    }
                }

                // Tarjeta 2: Irreparables
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFB2B2).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Cancel, null, tint = Color(0xFFFF7B7B), modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp)) // Corregí este Spacer que tenías en 1.dp
                        Text("03", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Text("Mantenimientos irreparables", fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
                    }
                }
            }

            // --- SECCIÓN DE CONFIGURACIÓN (Sin cambios de lógica, solo espaciado) ---
            Text(
                text = "CONFIGURACIÓN",
                modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF747D8C)
            )

            // Botón Cambiar Contraseña
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable { /* Acción */ },
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF8B93FF).copy(alpha = 0.1f),
                        modifier = Modifier.size(45.dp)
                    ) {
                        Icon(Icons.Default.Password, null, tint = Color(0xFF8B93FF), modifier = Modifier.padding(10.dp))
                    }
                    Text(
                        text = "Cambiar contraseña",
                        modifier = Modifier.padding(start = 16.dp).weight(1f),
                        color = Color(0xFF8B93FF),
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF8B93FF))
                }
            }

            // Botón Cerrar Sesión
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable { /* Acción */ },
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFE5E5),
                        modifier = Modifier.size(45.dp)
                    ) {
                        Icon(Icons.Default.Logout, null, tint = Color(0xFFE74C3C), modifier = Modifier.padding(10.dp))
                    }
                    Text(
                        text = "Cerrar sesión",
                        modifier = Modifier.padding(start = 16.dp).weight(1f),
                        color = Color(0xFFE74C3C),
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFFFB2B2))
                }
            }
        }
    }
}

@Composable
@Preview
fun mesi() {
    EmpleadoPerfil()
}