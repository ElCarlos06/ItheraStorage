package com.example.activos360.ui.screens.Empleado.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.activos360.ui.components.Buttons
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.activos360.ui.components.HeaderRegresar
import com.example.activos360.ui.components.MainAssetCard

@Composable
fun ConfirmarResguardoScreen(onBack: () -> Unit, onConfirm: () -> Unit) {
    // Estados para los Checkboxes (Esto debería ir en un ViewModel después)
    val checks = remember { mutableStateListOf(false, false, false, false) }

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.padding(24.dp)) {
                Buttons(text = "Confirmar Resguardo", onClick = onConfirm)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderRegresar(titulo = "Confirmación de resguardo", onBackClick = onBack)

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(24.dp))

                // Reutilizamos la MainAssetCard que ya tienes en el otro archivo
                MainAssetCard(id = "ACTIVO #0482", nombre = "MacBook Pro 16\"")

                Spacer(modifier = Modifier.height(32.dp))

                // Filas de Checkboxes
                val opciones = listOf("¿Enciende correctamente?", "Pantalla sin daños", "Incluye cargador original", "Sin daños estéticos")
                opciones.forEachIndexed { index, texto ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = texto, color = Color(0xFF2D3436), fontSize = 15.sp)
                        Checkbox(
                            checked = checks[index],
                            onCheckedChange = { checks[index] = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF7B88FF))
                        )
                    }
                    Divider(color = Color(0xFFF1F2F6), thickness = 1.dp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sección de Evidencias (Fotos)
                Text(
                    text = "Evidencias",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2D3436)
                )
                // Sección de Evidencias (Fotos)
                Text(
                    text = "Max 3 fts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Gray,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cuadros de fotos (puedes crear un componente para esto)
                    FotoPlaceholder()
                    FotoPlaceholder()
                    FotoPlaceholder()
                }
            }
        }
    }
}

@Composable
fun FotoPlaceholder() {
    Surface(
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8F9FF),
        border = BorderStroke(1.dp, Color(0xFF7B88FF).copy(alpha = 0.2f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = null,
                tint = Color(0xFF7B88FF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
@Preview
fun previewConfirmarResguardo() {
    ConfirmarResguardoScreen({}, {})
}

