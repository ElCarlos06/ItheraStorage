package com.example.activos360.ui.screens.Empleado.details

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.EvidenciasSection
import com.example.activos360.ui.components.HeaderRegresar
import com.example.activos360.ui.components.MainAssetCard
import com.example.activos360.ui.viewmodel.AssetDetailViewModel

// Tres estados posibles por cada ítem del checklist
private enum class CheckState { OK, FALLA, NO_APLICA }

private val CHECKLIST_ITEMS = listOf(
    "¿Enciende correctamente?",
    "Pantalla sin daños",
    "Incluye cargador original",
    "Sin daños estéticos"
)

@Composable
fun ConfirmarResguardoScreen(
    activoId: Long,
    onBack: () -> Unit,
    onConfirmed: () -> Unit,
    onReportarDano: () -> Unit = {},
    viewModel: AssetDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val vmState = viewModel.uiState   // Observamos el estado del ViewModel

    // Estado inicial: FALLA para que el empleado confirme activamente cada punto
    val checks = remember { mutableStateListOf(*Array(CHECKLIST_ITEMS.size) { CheckState.FALLA }) }
    var fotos by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showModalFallas by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { if (fotos.size < 3) fotos = fotos + it }
    }

    // Construye el checklist y llama al ViewModel.
    // afterConfirm determina a dónde navegar cuando el backend responde OK:
    //   onConfirmed    → flujo normal (volver al home)
    //   onReportarDano → confirmar resguardo y abrir reporte de daño
    fun doConfirmar(afterConfirm: () -> Unit = onConfirmed) {
        val checklistStr = CHECKLIST_ITEMS.mapIndexed { i, nombre ->
            val estado = when (checks.getOrElse(i) { CheckState.FALLA }) {
                CheckState.OK        -> "OK"
                CheckState.NO_APLICA -> "N/A"
                CheckState.FALLA     -> "NO"
            }
            "$nombre=$estado"
        }.joinToString("; ")
        val observaciones = "Checklist: $checklistStr | Fotos: ${fotos.size}"
        viewModel.confirmarResguardo(
            activoId = activoId,
            observaciones = observaciones,
            fotos = fotos,
            context = context,
            onSuccess = afterConfirm   // el ViewModel llama esto en Dispatchers.Main al éxito
        )
    }

    // ── Diálogo de error del ViewModel ──────────────────────────────────────────
    vmState.errorMessage?.let { err ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = {
                Text(
                    text = "No se pudo confirmar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = { Text(text = err, fontSize = 14.sp, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("Entendido", color = Color(0xFF7B88FF))
                }
            }
        )
    }

    // ── Modal: ítems con posible falla ──────────────────────────────────────────
    if (showModalFallas) {
        AlertDialog(
            onDismissRequest = { showModalFallas = false },
            title = {
                Text(
                    text = "Posibles daños detectados",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Uno o más puntos del checklist no fueron marcados como OK. " +
                            "Se confirmará el resguardo y a continuación podrás generar " +
                            "un reporte de daño. ¿Deseas continuar?",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showModalFallas = false
                    doConfirmar(afterConfirm = onReportarDano)   // confirma → reporte de daño
                }) {
                    Text(
                        text = "Sí, reportar daño",
                        color = Color(0xFFE53E3E),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showModalFallas = false
                    doConfirmar(afterConfirm = onConfirmed)      // confirma → home normal
                }) {
                    Text(text = "No, cancelar", color = Color(0xFF7B88FF))
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Box(modifier = Modifier.padding(24.dp)) {
                Buttons(
                    text = if (vmState.isLoading) "Confirmando..." else "Confirmar Resguardo",
                    enabled = !vmState.isLoading,
                    onClick = {
                        // Si algún ítem sigue en FALLA (no confirmado OK ni marcado N/A) → modal
                        val tieneFallas = checks.any { it == CheckState.FALLA }
                        if (tieneFallas) {
                            showModalFallas = true
                        } else {
                            doConfirmar(afterConfirm = onConfirmed)
                        }
                    }
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
            HeaderRegresar(titulo = "Confirmación de resguardo", onBackClick = onBack)
            Spacer(modifier = Modifier.height(10.dp))

            MainAssetCard(id = "ACTIVO #$activoId", nombre = "Activo")

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                CHECKLIST_ITEMS.forEachIndexed { index, texto ->
                    val estado = checks[index]
                    val esNA = estado == CheckState.NO_APLICA

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Texto del ítem — atenuado si N/A
                        Text(
                            text = texto,
                            color = if (esNA) Color(0xFFB2BEC3) else Color(0xFF2D3436),
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )

                        // Chip "N/A" — alterna entre NO_APLICA y FALLA
                        Surface(
                            onClick = {
                                checks[index] =
                                    if (esNA) CheckState.FALLA else CheckState.NO_APLICA
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = if (esNA) Color(0xFFEEEFF8) else Color.Transparent,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (esNA) Color(0xFF7B88FF) else Color(0xFFDFE3E8)
                            )
                        ) {
                            Text(
                                text = "N/A",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = if (esNA) FontWeight.Bold else FontWeight.Normal,
                                color = if (esNA) Color(0xFF7B88FF) else Color(0xFFB2BEC3)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Checkbox OK — deshabilitado si N/A está activo
                        Checkbox(
                            checked = estado == CheckState.OK,
                            onCheckedChange = { checked ->
                                if (!esNA) {
                                    checks[index] = if (checked) CheckState.OK else CheckState.FALLA
                                }
                            },
                            enabled = !esNA,
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF7B88FF),
                                disabledUncheckedColor = Color(0xFFDFE3E8)
                            )
                        )
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F2F6))
                }

                Spacer(modifier = Modifier.height(32.dp))

                EvidenciasSection(
                    fotos = fotos,
                    onAddClick = { photoPickerLauncher.launch("image/*") },
                    onRemove = { index ->
                        fotos = fotos.toMutableList().also { it.removeAt(index) }
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun previewConfirmarResguardo() {
    ConfirmarResguardoScreen(activoId = 1, onBack = {}, onConfirmed = {})
}
