package com.example.activos360.ui.screens.tecnico

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
import com.example.activos360.ui.Screens.Empleado.TecnicoHome
import com.example.activos360.ui.components.BottomCustomBar
import com.example.activos360.ui.components.PerfilHeader

@Composable
fun UserProfile() {
    // Simulación de datos
    val nombreUsuario = "Israel Mena"
    val correoUsuario = "israel.mena@empresa.com"
    val rolUsuario = "Técnico"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PerfilHeader(
            nombre = nombreUsuario,
            rol = rolUsuario
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            Text(
                text = "Configuración",
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            // Botón Cambiar Contraseña
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable { /* Acción cambiar contraseña */ },
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE5E7FF),
                        modifier = Modifier.size(45.dp)
                    ) {
                        Icon(Icons.Default.Password, null, tint = Color(0xFF8B93FF), modifier = Modifier.padding(10.dp))
                    }
                    Text(
                        text = "Cambiar Contraseña",
                        modifier = Modifier.padding(start = 16.dp).weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF8B93FF))
                }
            }

            // Botón Cerrar Sesión
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable { /* Acción de cerrar sesión (navegar a login) */ },
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
fun UserProfilePreview() {
    UserProfile()
}
