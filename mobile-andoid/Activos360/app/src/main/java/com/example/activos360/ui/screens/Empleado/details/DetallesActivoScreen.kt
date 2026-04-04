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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.GridView
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
    val etiqueta = (activo["etiqueta"] as? String).orEmpty()
    val numeroSerie = (activo["numeroSerie"] as? String).orEmpty()
    val idModelo = (activo["idModelo"] as? Number)?.toLong()
        ?: (activo["idModelo"] as? String)?.toLongOrNull()
    val estadoOperativo = (activo["estadoOperativo"] as? String).orEmpty()
    val estadoCustodia = (activo["estadoCustodia"] as? String).orEmpty()
    val nombreMostrado = etiqueta.ifBlank { "Activo #$activoId" }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
               if (uiState.canResguardar) {
                    Buttons(text = "Resguardar", onClick = { onResguardarClick() })
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
                            Text(
                                text = "Cargando detalles...",
                                color = Color.Gray
                            )
                        }
                    } else if (!uiState.isLoading || activo.isNotEmpty()) {

                    uiState.errorMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = Color(0xFFD33030),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Tarjeta principal
                    MainAssetCard(id = "ACTIVO #$activoId", nombre = nombreMostrado)
                    Spacer(modifier = Modifier.height(16.dp))

                    InfoCard(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        label = "Etiqueta",
                        value = etiqueta.ifBlank { "-" }
                    )

                    InfoCard(
                        imageVector = Icons.Outlined.GridView,
                        label = "Modelo (id)",
                        value = idModelo?.toString() ?: "-"
                    )

                    CaracteristicasSeccion(
                        lista = listOfNotNull(
                            "Número de serie: ${numeroSerie.ifBlank { "-" }}",
                            "Estado operativo: ${estadoOperativo.ifBlank { "-" }}",
                            "Estatus: ${estadoCustodia.ifBlank { "-" }}"
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    val estatusActual = estadoCustodia.lowercase()

                    when (estatusActual) {
                        "en proceso", "enproceso", "proc" -> {
                            // Solo si está en proceso Y te pertenece (canResguardar)
                            if (uiState.canResguardar) {
                               /* Buttons(
                                    text = "Resguardar Activo",
                                    onClick = { onResguardarClick() }
                                )*/
                            } else {
                                // Está en proceso pero asignado a OTRO empleado. Solo lectura.
                                Text(
                                    text = "Este activo está en proceso de asignación pero no esta a tu cargo.",
                                    color = Color.Gray,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        "resguardado", "resguardo", "resg" -> {
                            val opActual = estadoOperativo.lowercase()
                            when {
                                opActual.contains("reportado") -> Text(
                                    text = "Este activo tiene un reporte activo. No se pueden realizar acciones hasta que sea atendido.",
                                    color = Color(0xFFD33030),
                                    modifier = Modifier.padding(16.dp)
                                )
                                opActual.contains("mantenimiento") -> Text(
                                    text = "Este activo está en mantenimiento. No se pueden realizar acciones hasta que sea liberado.",
                                    color = Color.Gray,
                                    modifier = Modifier.padding(16.dp)
                                )
                                else -> {
                                    Buttons(
                                        text = "Reportar daño",
                                        onClick = { onReportarDanoClick(activoId, etiqueta.ifBlank { "ACTIVO #$activoId" }, nombreMostrado) }
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Buttons(
                                        text = "Devolver Activo",
                                        onClick = { onDevolverActivoClick(activoId, etiqueta.ifBlank { "ACTIVO #$activoId" }, nombreMostrado) }
                                    )
                                }
                            }
                        }

                        "disponible", "disp" -> {
                            // No le pertenece a nadie, es de solo lectura.
                            Text(
                                text = "El Activo esta disponible, pero no esta a tu cargo.",
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
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
    DetallesActivoScreen(activoId = 1)
}