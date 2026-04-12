package com.example.activos360.ui.screens.Empleado.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Category
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.CaracteristicasSeccion
import com.example.activos360.ui.components.HeaderRegresar
import com.example.activos360.ui.components.InfoCard
import com.example.activos360.ui.components.MainAssetCard
import com.example.activos360.ui.viewmodel.AssetDetailViewModel

@Composable
fun DetallesActivoScreen(
    activoId: Long,
    onBack: () -> Unit = {},
    onResguardarClick: () -> Unit = {},
    onReportarDanoClick: (Long, String, String) -> Unit = { _, _, _ -> },
    onDevolverActivoClick: (Long, String, String) -> Unit = { _, _, _ -> },
    viewModel: AssetDetailViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    LaunchedEffect(activoId) {
        viewModel.loadActivo(activoId)
    }

    val activo = uiState.activo.orEmpty()

    // ── Campos básicos ────────────────────────────────────────────────────────
    val etiqueta        = (activo["etiqueta"]        as? String).orEmpty()
    val numeroSerie     = (activo["numeroSerie"]     as? String).orEmpty()
    val estadoOperativo = (activo["estadoOperativo"] as? String).orEmpty()
    val estadoCustodia  = (activo["estadoCustodia"]  as? String).orEmpty()
    val descripcion     = (activo["descripcion"]     as? String).orEmpty()
    val nombreMostrado  = etiqueta.ifBlank { "Activo #$activoId" }

    // ── TipoActivo (marca y modelo viven aquí en el back) ─────────────────────
    @Suppress("UNCHECKED_CAST")
    val tipoActivoMap   = activo["tipoActivo"] as? Map<String, Any?>
    val tipoActivoNombre = (tipoActivoMap?.get("nombre") as? String).orEmpty()
    val tipoActivoMarca  = (tipoActivoMap?.get("marca")  as? String).orEmpty()
    val tipoActivoModelo = (tipoActivoMap?.get("modelo") as? String).orEmpty()

    // ── Costo (BigDecimal llega como Double/Number en Moshi) ──────────────────
    val costoDisplay = when (val c = activo["costo"]) {
        is Number -> "$%.2f".format(c.toDouble())
        is String -> c.toDoubleOrNull()?.let { "$%.2f".format(it) }
        else      -> null
    }

    // ── Empleado que resguarda (primer resguardo confirmado) ──────────────────
    @Suppress("UNCHECKED_CAST")
    val empleadoResguardante = uiState.resguardos
        .firstOrNull { r ->
            val estado = (r["estadoResguardo"] as? String)?.trim()?.lowercase()
            estado == "confirmado" || estado == "resguardado"
        }
        ?.let { r ->
            val emp = r["usuarioEmpleado"] as? Map<String, Any?>
            (emp?.get("nombreCompleto") as? String)
                ?: (emp?.get("nombre") as? String)
        }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                if (uiState.canResguardar) {
                    Buttons(text = "Resguardar", onClick = onResguardarClick)
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
            HeaderRegresar(titulo = "Detalles del activo", onBackClick = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    if (uiState.isLoading && activo.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFF7B88FF))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Cargando detalles...", color = Color.Gray)
                        }
                    } else {

                        uiState.errorMessage?.let { msg ->
                            Text(
                                text = msg,
                                color = Color(0xFFD33030),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // ── Card 1: Tipo de activo (texto pequeño) + Etiqueta (texto grande) ──
                        MainAssetCard(
                            id = tipoActivoNombre.ifBlank { "Activo" },
                            nombre = etiqueta.ifBlank { "ACTIVO #$activoId" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // ── Card 2: Marca ─────────────────────────────────────────────────────
                        InfoCard(
                            imageVector = Icons.Outlined.BusinessCenter,
                            label = "Marca",
                            value = tipoActivoMarca.ifBlank { "-" }
                        )

                        // ── Card 3: Modelo ────────────────────────────────────────────────────
                        InfoCard(
                            imageVector = Icons.Outlined.Build,
                            label = "Modelo",
                            value = tipoActivoModelo.ifBlank { "-" }
                        )

                        // ── Card 4: Características ───────────────────────────────────────────
                        CaracteristicasSeccion(
                            lista = listOfNotNull(
                                tipoActivoNombre.takeIf { it.isNotBlank() }
                                    ?.let { "Tipo de activo: $it" },
                                numeroSerie.takeIf { it.isNotBlank() }
                                    ?.let { "Número de serie: $it" },
                                costoDisplay
                                    ?.let { "Costo: $it" },
                                descripcion.takeIf { it.isNotBlank() }
                                    ?.let { "Descripción: $it" },
                                empleadoResguardante?.takeIf { it.isNotBlank() }
                                    ?.let { "Resguardado por: $it" }
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ── Acciones según estado ─────────────────────────────────────────────
                        val estatusActual  = estadoCustodia.trim().lowercase()
                        val operativoActual = estadoOperativo.trim().lowercase()

                        when {
                            // Activo en mantenimiento → solo lectura para todos
                            operativoActual.contains("mantenimiento") -> Text(
                                text = "Este activo está en mantenimiento activo.",
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            // Activo reportado → solo lectura para todos
                            operativoActual.contains("reportado") -> Text(
                                text = "Este activo tiene un reporte activo pendiente de atención.",
                                color = Color(0xFFD33030),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            // En proceso de asignación → solo si le pertenece (canResguardar)
                            estatusActual in listOf("en proceso", "enproceso", "en_proceso", "proc") -> {
                                if (!uiState.canResguardar) {
                                    Text(
                                        text = "Este activo está en proceso de asignación, pero no está a tu cargo.",
                                        color = Color.Gray,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                                // Si canResguardar == true, el botón "Resguardar" ya aparece en el bottomBar
                            }

                            // Resguardado: solo el empleado con resguardo confirmado puede actuar
                            estatusActual in listOf("resguardado", "resguardo", "resg") -> {
                                if (uiState.esResguardadoPorMiUsuario) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp)
                                    ) {
                                        Buttons(
                                            text = "Reportar daño",
                                            onClick = {
                                                onReportarDanoClick(
                                                    activoId,
                                                    etiqueta.ifBlank { "ACTIVO #$activoId" },
                                                    nombreMostrado
                                                )
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Buttons(
                                            text = "Devolver Activo",
                                            onClick = {
                                                onDevolverActivoClick(
                                                    activoId,
                                                    etiqueta.ifBlank { "ACTIVO #$activoId" },
                                                    nombreMostrado
                                                )
                                            }
                                        )
                                    }
                                } else {
                                    // Técnico u otro empleado: solo lectura
                                    Text(
                                        text = "Este activo está bajo el resguardo de otro empleado.",
                                        color = Color.Gray,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }

                            // Disponible → nadie puede actuar desde aquí
                            estatusActual in listOf("disponible", "disp") -> Text(
                                text = "Este activo está disponible y no está a tu cargo.",
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun preview() {
    DetallesActivoScreen(activoId = 1L)
}
