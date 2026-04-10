package com.example.activos360.ui.screens.tecnico

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.EvidenciasSection
import com.example.activos360.ui.components.HeaderRegresar
import com.example.activos360.ui.components.MainAssetCard
import com.example.activos360.ui.viewmodel.GenerarReporteViewModel

@Composable
fun GenerarReporteScreen(
    activoId: Long,
    mantenimientoId: Long,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: GenerarReporteViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    var diagnostico by remember { mutableStateOf("") }
    var accionesRealizadas by remember { mutableStateOf("") }
    var piezasUtilizadas by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    // null = sin selección, "Reparado" o "Irreparable"
    var resultado by remember { mutableStateOf<String?>(null) }
    val fotos = remember { mutableStateListOf<Uri>() }

    var showModalIrreparable by remember { mutableStateOf(false) }
    var showModalIncompleto by remember { mutableStateOf(false) }

    LaunchedEffect(activoId, mantenimientoId) {
        viewModel.load(activoId, mantenimientoId)
    }

    val fotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && fotos.size < 3) fotos.add(uri)
    }

    // Modal: confirmar irreparable (dar de baja activo)
    if (showModalIrreparable) {
        AlertDialog(
            onDismissRequest = { showModalIrreparable = false },
            title = {
                Text(
                    text = "¿Confirmar equipo irreparable?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Esta acción enviará una solicitud para dar de baja el activo. El estado operativo cambiará a \"Baja\" y no podrá revertirse fácilmente. ¿Estás seguro?",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showModalIrreparable = false
                    viewModel.cerrarMantenimiento(
                        diagnostico = diagnostico,
                        accionesRealizadas = accionesRealizadas,
                        piezasUtilizadas = piezasUtilizadas,
                        observaciones = observaciones,
                        resultado = "Irreparable",
                        fotos = fotos.toList(),
                        context = context,
                        onSuccess = onSuccess
                    )
                }) {
                    Text("Confirmar baja", color = Color(0xFFE53E3E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showModalIrreparable = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // Modal: campos obligatorios incompletos
    if (showModalIncompleto) {
        AlertDialog(
            onDismissRequest = { showModalIncompleto = false },
            title = { Text("Campos incompletos", fontWeight = FontWeight.Bold) },
            text = { Text("El diagnóstico técnico y el resultado del mantenimiento son obligatorios.") },
            confirmButton = {
                TextButton(onClick = { showModalIncompleto = false }) {
                    Text("Entendido", color = Color(0xFF7B88FF))
                }
            }
        )
    }

    // Dialog de error de red/servidor
    uiState.errorMessage?.let { err ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(err) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK", color = Color(0xFF7B88FF))
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            if (!uiState.isLoading) {
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Buttons(
                        text = if (uiState.isGuardando) "Guardando..." else "Finalizar mantenimiento",
                        enabled = !uiState.isGuardando,
                        onClick = {
                            if (diagnostico.isBlank() || resultado == null) {
                                showModalIncompleto = true
                                return@Buttons
                            }
                            when (resultado) {
                                "Reparado" -> viewModel.cerrarMantenimiento(
                                    diagnostico = diagnostico,
                                    accionesRealizadas = accionesRealizadas,
                                    piezasUtilizadas = piezasUtilizadas,
                                    observaciones = observaciones,
                                    resultado = "Reparado",
                                    fotos = fotos.toList(),
                                    context = context,
                                    onSuccess = onSuccess
                                )
                                "Irreparable" -> showModalIrreparable = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color.White)
        ) {
            HeaderRegresar(titulo = "Cerrar\nMantenimiento", onBackClick = onBack)

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF7B88FF))
                }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                MainAssetCard(
                    id = uiState.activoEtiqueta,
                    nombre = uiState.activoNombre
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campos del formulario
                CampoTexto(
                    label = "Diagnóstico técnico *",
                    placeholder = "Describe el diagnóstico detallado...",
                    value = diagnostico,
                    onValueChange = { diagnostico = it },
                    minLines = 4
                )

                CampoTexto(
                    label = "Acciones realizadas",
                    placeholder = "Detalla las reparaciones efectuadas...",
                    value = accionesRealizadas,
                    onValueChange = { accionesRealizadas = it },
                    minLines = 4
                )

                CampoTexto(
                    label = "Piezas utilizadas",
                    placeholder = "Lista de repuestos utilizados...",
                    value = piezasUtilizadas,
                    onValueChange = { piezasUtilizadas = it },
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sección de evidencias
                EvidenciasSection(
                    fotos = fotos,
                    maxFotos = 3,
                    onAddClick = { fotoPicker.launch("image/*") },
                    onRemove = { index -> fotos.removeAt(index) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                CampoTexto(
                    label = "Observaciones",
                    placeholder = "Notas adicionales del cierre...",
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sección resultado
                Text(
                    text = "Resultado del mantenimiento *",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF2D3436)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BotonResultado(
                        text = "Equipo Reparado",
                        seleccionado = resultado == "Reparado",
                        colorActivo = Color(0xFF2ECC71),
                        modifier = Modifier.weight(1f),
                        onClick = { resultado = "Reparado" }
                    )
                    BotonResultado(
                        text = "Equipo Irreparable",
                        seleccionado = resultado == "Irreparable",
                        colorActivo = Color(0xFFE53E3E),
                        modifier = Modifier.weight(1f),
                        onClick = { resultado = "Irreparable" }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun CampoTexto(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF2D3436),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(text = placeholder, color = Color(0xFFB2BEC3), fontSize = 14.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7B88FF),
                unfocusedBorderColor = Color(0xFFE8E9F3),
                focusedContainerColor = Color(0xFFF8F9FE),
                unfocusedContainerColor = Color(0xFFF8F9FE)
            ),
            minLines = minLines
        )
    }
}

@Composable
private fun BotonResultado(
    text: String,
    seleccionado: Boolean,
    colorActivo: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .then(
                if (!seleccionado) Modifier.border(1.5.dp, Color(0xFFE0E0E0), RoundedCornerShape(14.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) colorActivo else Color(0xFFF8F9FE),
            contentColor = if (seleccionado) Color.White else Color(0xFF636E72)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (seleccionado) 3.dp else 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
        )
    }
}
