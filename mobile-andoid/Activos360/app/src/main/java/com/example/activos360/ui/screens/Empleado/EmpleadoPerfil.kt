package com.example.activos360.ui.screens.Empleado

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.activos360.ui.components.MoonIcon
import com.example.activos360.ui.components.MoonIcons
import com.example.activos360.ui.components.PerfilHeader
import com.example.activos360.ui.viewmodel.EmpleadoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(
    viewModel: EmpleadoViewModel = viewModel(),
    onNavigateToChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    val nombreUsuario = viewModel.nombreUsuario ?: "Usuario Desconocido"
    val rolUsuario    = viewModel.rolUsuario    ?: "Empleado"
    val fotoUrl       = viewModel.fotoUsuario

    var showPhotoMenu   by remember { mutableStateOf(false) }
    var showPhotoViewer by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.subirFoto(uri, context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PerfilHeader(
            viewModel = viewModel,
            nombre    = nombreUsuario,
            rol       = rolUsuario,
            onEditPhotoClick = { showPhotoMenu = true }
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
                        MoonIcon(
                            icon = MoonIcons.SecurityKey,
                            contentDescription = null,
                            tint = Color(0xFF8B93FF),
                            size = 24.dp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Text(
                        text = "Cambiar Contraseña",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    MoonIcon(
                        icon = MoonIcons.ControlsChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF8B93FF),
                        size = 20.dp
                    )
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
                        MoonIcon(
                            icon = MoonIcons.SoftwareLogout,
                            contentDescription = null,
                            tint = Color(0xFFE74C3C),
                            size = 24.dp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Text(
                        text = "Cerrar sesión",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                        color = Color(0xFFE74C3C),
                        fontWeight = FontWeight.SemiBold
                    )
                    MoonIcon(
                        icon = MoonIcons.ControlsChevronRight,
                        contentDescription = null,
                        tint = Color(0xFFFFB2B2),
                        size = 20.dp
                    )
                }
            }
        }
    }

    // ── Bottom Sheet: opciones de foto ───────────────────────────────────────
    if (showPhotoMenu) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoMenu = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Foto de perfil",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )

                HorizontalDivider(color = Color(0xFFEEEEEE))

                // Opción: Ver foto
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !fotoUrl.isNullOrBlank()) {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showPhotoMenu   = false
                                showPhotoViewer = true
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE5E7FF),
                        modifier = Modifier.size(40.dp)
                    ) {
                        MoonIcon(
                            icon = MoonIcons.ControlsEye,
                            contentDescription = null,
                            tint = Color(0xFF7B88FF),
                            size = 20.dp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Ver foto",
                            fontWeight = FontWeight.SemiBold,
                            color = if (!fotoUrl.isNullOrBlank()) Color.DarkGray else Color.LightGray
                        )
                        if (fotoUrl.isNullOrBlank()) {
                            Text(
                                text = "Sin foto de perfil",
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(horizontal = 24.dp))

                // Opción: Editar foto
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showPhotoMenu = false
                                imagePickerLauncher.launch("image/*")
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE5E7FF),
                        modifier = Modifier.size(40.dp)
                    ) {
                        MoonIcon(
                            icon = MoonIcons.GenericEdit,
                            contentDescription = null,
                            tint = Color(0xFF7B88FF),
                            size = 20.dp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Text(
                        text = "Editar foto",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }

    // ── Visor de foto a pantalla completa ────────────────────────────────────
    if (showPhotoViewer && !fotoUrl.isNullOrBlank()) {
        Dialog(
            onDismissRequest = { showPhotoViewer = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showPhotoViewer = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = fotoUrl,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(0.dp))
                )
                // Botón cerrar (esquina superior derecha)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .clickable { showPhotoViewer = false },
                    contentAlignment = Alignment.Center
                ) {
                    MoonIcon(
                        icon = MoonIcons.ControlsClose,
                        contentDescription = "Cerrar",
                        tint = Color.White,
                        size = 20.dp
                    )
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
