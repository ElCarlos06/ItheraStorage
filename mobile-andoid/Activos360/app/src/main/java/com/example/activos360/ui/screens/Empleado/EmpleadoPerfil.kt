package com.example.activos360.ui.screens.Empleado

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.components.MoonIcon
import com.example.activos360.ui.components.MoonIcons
import com.example.activos360.ui.components.PerfilHeader
import com.example.activos360.ui.viewmodel.EmpleadoViewModel

@Composable
fun UserProfile(
    viewModel: EmpleadoViewModel = viewModel(),
    onNavigateToChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    val nombreUsuario = viewModel.nombreUsuario ?: "Usuario Desconocido"
    val rolUsuario = viewModel.rolUsuario ?: "Empleado"

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.subirFoto(uri, context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PerfilHeader(
            viewModel = viewModel,
            nombre = nombreUsuario,
            rol = rolUsuario,
            onEditPhotoClick = { imagePickerLauncher.launch("image/*") }
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
                    .clickable { onNavigateToChangePassword() },
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
                        MoonIcon(icon = MoonIcons.SecurityKey, contentDescription = null, tint = Color(0xFF8B93FF), size = 24.dp, modifier = Modifier.padding(10.dp))
                    }
                    Text(
                        text = "Cambiar Contraseña",
                        modifier = Modifier.padding(start = 16.dp).weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    MoonIcon(icon = MoonIcons.ControlsChevronRight, contentDescription = null, tint = Color(0xFF8B93FF), size = 20.dp)
                }
            }

            // Botón Cerrar Sesión
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable { onLogout() },
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
                        MoonIcon(icon = MoonIcons.SoftwareLogOut, contentDescription = null, tint = Color(0xFFE74C3C), size = 24.dp, modifier = Modifier.padding(10.dp))
                    }
                    Text(
                        text = "Cerrar sesión",
                        modifier = Modifier.padding(start = 16.dp).weight(1f),
                        color = Color(0xFFE74C3C),
                        fontWeight = FontWeight.SemiBold
                    )
                    MoonIcon(icon = MoonIcons.ControlsChevronRight, contentDescription = null, tint = Color(0xFFFFB2B2), size = 20.dp)
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
