package com.example.activos360.ui.screens.tecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.CaracteristicasSeccion
import com.example.activos360.ui.components.FilaPrioridad
import com.example.activos360.ui.components.FilaTipoFalla
import com.example.activos360.ui.components.HeaderRegresar
import com.example.activos360.ui.components.InfoCard
import com.example.activos360.ui.components.MainAssetCard
import com.example.activos360.ui.components.SeccionDescripcion
import com.example.activos360.ui.components.SeccionEvidencia
import com.example.activos360.ui.components.SeccionUsuarioReporte
import com.example.activos360.ui.viewmodel.TecnicoReporteViewModel

@Composable
fun ReportesTecnicoScreen(
    activoId: Long,
    mantenimientoId: Long,
    onBack: () -> Unit,
    viewModel: TecnicoReporteViewModel = viewModel()
) {
    var tabSeleccionada by remember { mutableStateOf(0) }
    val uiState = viewModel.uiState

    LaunchedEffect(activoId, mantenimientoId) {
        viewModel.load(activoId, mantenimientoId)
    }

    Scaffold(
        bottomBar = {
            // "Atender" solo aparece en la pestaña Reporte cuando el técnico puede atender
            if (tabSeleccionada == 1 && uiState.puedeAtender) {
                Box(modifier = Modifier.padding(24.dp)) {
                    Buttons(
                        text = if (uiState.isAtendiendo) "Procesando..." else "Atender",
                        enabled = !uiState.isAtendiendo,
                        onClick = { viewModel.atender() }
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
            HeaderRegresar(titulo = "Detalles del activo y\nel reporte", onBackClick = onBack)

            Spacer(modifier = Modifier.height(24.dp))

            SelectorPestañas(
                seleccionada = tabSeleccionada,
                onTabSelected = { tabSeleccionada = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF7B88FF))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Cargando...", color = Color.Gray)
                }
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = Color(0xFFD33030),
                    modifier = Modifier.padding(24.dp)
                )
            } else if (tabSeleccionada == 0) {
                TabInformacion(uiState.activoEtiqueta, uiState.activoNombre, uiState.activoNumeroSerie, uiState.activoEstadoOperativo, uiState.activoEstadoCustodia, activoId)
            } else {
                TabReporte(
                    etiqueta = uiState.activoEtiqueta,
                    nombre = uiState.activoNombre,
                    tipoFalla = uiState.tipoFalla,
                    prioridad = uiState.prioridad,
                    reporterNombre = uiState.reporterNombre,
                    reporterFotoUrl = uiState.reporterFotoUrl,
                    fechaDisplay = uiState.fechaDisplay,
                    descripcion = uiState.descripcion,
                    evidenciaUrls = uiState.evidenciaUrls
                )
            }
        }
    }
}

@Composable
private fun TabInformacion(
    etiqueta: String,
    nombre: String,
    numeroSerie: String,
    estadoOperativo: String,
    estadoCustodia: String,
    activoId: Long
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainAssetCard(
            id = etiqueta.ifBlank { "ACTIVO #$activoId" },
            nombre = nombre.ifBlank { "Activo #$activoId" }
        )
        Spacer(modifier = Modifier.height(16.dp))

        InfoCard(
            imageVector = Icons.Outlined.BookmarkBorder,
            label = "Etiqueta",
            value = etiqueta.ifBlank { "-" }
        )
        InfoCard(
            imageVector = Icons.Outlined.GridView,
            label = "Estado custodia",
            value = estadoCustodia.ifBlank { "-" }
        )

        CaracteristicasSeccion(
            lista = listOfNotNull(
                "Número de serie: ${numeroSerie.ifBlank { "-" }}",
                "Estado operativo: ${estadoOperativo.ifBlank { "-" }}"
            )
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun TabReporte(
    etiqueta: String,
    nombre: String,
    tipoFalla: String,
    prioridad: String,
    reporterNombre: String,
    reporterFotoUrl: String?,
    fechaDisplay: String,
    descripcion: String,
    evidenciaUrls: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        MainAssetCard(
            id = etiqueta.ifBlank { "Activo" },
            nombre = nombre.ifBlank { etiqueta }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FilaTipoFalla(titulo = "Tipo de falla", valor = tipoFalla.ifBlank { "-" })
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F2F6))

        FilaPrioridad(prioridad = prioridad.ifBlank { "MEDIA" })
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F2F6))

        SeccionUsuarioReporte(
            fotoUrl = reporterFotoUrl,
            nombreUsuario = reporterNombre.ifBlank { "Usuario" },
            etiquetaReporte = "Reportado por",
            fechaCompleta = fechaDisplay
        )
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F2F6))

        SeccionDescripcion(cuerpo = descripcion)
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F2F6))

        SeccionEvidencia(imagenes = evidenciaUrls)

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SelectorPestañas(seleccionada: Int, onTabSelected: (Int) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(56.dp),
        shape = RoundedCornerShape(50.dp),
        color = Color(0xFFF8F9FF)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PestañaItem(
                titulo = "Información",
                estaActiva = seleccionada == 0,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(0) }
            )
            PestañaItem(
                titulo = "Reporte",
                estaActiva = seleccionada == 1,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(1) }
            )
        }
    }
}

@Composable
fun PestañaItem(titulo: String, estaActiva: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(50.dp),
        color = if (estaActiva) Color(0xFF7B88FF) else Color.Transparent,
        contentColor = if (estaActiva) Color.White else Color.Gray
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = titulo,
                fontWeight = if (estaActiva) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}
