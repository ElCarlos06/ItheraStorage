package com.example.activos360.ui.screens.Empleado.details

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.EvidenciasSection
import com.example.activos360.ui.components.HeaderRegresar
import com.example.activos360.ui.components.MainAssetCard
import com.example.activos360.ui.viewmodel.AssetDetailViewModel

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
    viewModel: AssetDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val checks = remember { mutableStateListOf(false, false, false, false) }
    var fotos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { if (fotos.size < 3) fotos = fotos + it }
    }

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.padding(24.dp)) {
                Buttons(
                    text = "Confirmar Resguardo",
                    onClick = {
                        val checklistStr = CHECKLIST_ITEMS.mapIndexed { i, nombre ->
                            "$nombre=${if (checks.getOrElse(i) { false }) "OK" else "NO"}"
                        }.joinToString("; ")
                        val observaciones = "Checklist: $checklistStr | Fotos: ${fotos.size}"
                        viewModel.confirmarResguardo(
                            activoId = activoId,
                            observaciones = observaciones,
                            fotos = fotos,
                            context = context,
                            onSuccess = {
                                onConfirmed()
                            }
                        )
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
