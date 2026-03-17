package com.example.activos360.ui.screens.Empleado.details

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.ChipSelector
import com.example.activos360.ui.components.DropdownSelector
import com.example.activos360.ui.components.EvidenciasSection
import com.example.activos360.ui.components.FieldLabel
import com.example.activos360.ui.components.HeaderRegresar
import com.example.activos360.ui.components.MainAssetCard
import com.example.activos360.ui.viewmodel.ReportarViewModel

private val DodoriaWarning = Color(0xFFD33030).copy(alpha = 0.56f)

@Composable
fun ReportarDanoScreen(
    activoId: Long,
    activoEtiqueta: String,
    activoNombre: String,
    onBack: () -> Unit,
    onReportarSuccess: () -> Unit = {}
) {
    var selectedTipoNombre by remember { mutableStateOf("") }
    var selectedPrioridadNivel by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fotos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val viewModel: ReportarViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    val tiposFalla = uiState.tiposFalla
    val prioridades = uiState.prioridades
    val tipoOptions = tiposFalla.map { it.nombre }
    val prioridadOptions = prioridades.map { it.nivel }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { if (fotos.size < 3) fotos = fotos + it }
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = DodoriaWarning,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Buttons(
                    text = if (uiState.isLoading) "Enviando..." else "Reportar",
                    onClick = {
                        val idTipo = tiposFalla.find { it.nombre == selectedTipoNombre }?.id
                        val idPrioridad = prioridades.find { it.nivel == selectedPrioridadNivel }?.id
                        if (idTipo == null || idPrioridad == null || descripcion.isBlank()) return@Buttons
                        viewModel.reportar(
                            activoId = activoId,
                            idTipoFalla = idTipo,
                            idPrioridad = idPrioridad,
                            descripcionFalla = descripcion.trim(),
                            onSuccess = onReportarSuccess
                        )
                    },
                    enabled = !uiState.isLoading &&
                        selectedTipoNombre.isNotBlank() &&
                        selectedPrioridadNivel.isNotBlank() &&
                        descripcion.isNotBlank()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color.White)
        ) {
            HeaderRegresar(titulo = "Reportar Daño", onBackClick = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                MainAssetCard(id = activoEtiqueta, nombre = activoNombre)

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                DropdownSelector(
                    label = "Tipo de falla",
                    value = selectedTipoNombre,
                    placeholder = if (tipoOptions.isEmpty()) "Cargando..." else "Selecciona el tipo de daño",
                    options = tipoOptions,
                    onSelect = { selectedTipoNombre = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                FieldLabel("Descripción del problema")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    placeholder = {
                        Text(
                            "Describe detalladamente qué sucedió...",
                            color = Color(0xFF9CA3AF),
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF8F9FE),
                        focusedContainerColor = Color(0xFFF8F9FE),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF7B88FF)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF2D3436))
                )

                Spacer(modifier = Modifier.height(24.dp))

                ChipSelector(
                    label = "Prioridad",
                    options = prioridadOptions.ifEmpty { listOf("Baja", "Media", "Alta") },
                    selected = selectedPrioridadNivel,
                    onSelect = { selectedPrioridadNivel = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                EvidenciasSection(
                    fotos = fotos,
                    onAddClick = { photoPickerLauncher.launch("image/*") },
                    onRemove = { index ->
                        fotos = fotos.toMutableList().also { it.removeAt(index) }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ReportarDanoPreview() {
    ReportarDanoScreen(
        activoId = 1,
        activoEtiqueta = "ACTIVO #0482",
        activoNombre = "MacBook Pro 16\"",
        onBack = {}
    )
}
