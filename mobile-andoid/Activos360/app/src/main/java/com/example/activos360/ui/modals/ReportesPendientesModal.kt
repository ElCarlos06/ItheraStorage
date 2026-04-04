package com.example.activos360.ui.modal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activos360.ui.modals.ReporteItemCard

// Asegúrate de importar tu tarjeta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesPendientesModal(
    onDismiss: () -> Unit,
    onAtenderClick: (String) -> Unit
) {
    // Estado del BottomSheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = Color(0xFFF8F9FF), // Fondo grisáceo sutil
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Título del Modal
            Text(
                text = "Reportes Pendientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Lista de reportes (usamos LazyColumn para que tenga scroll)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Por ahora simulamos 3 reportes, luego vendrán de una lista real
                items(3) { index ->
                    ReporteItemCard(
                        onConfirmClick = {
                            onAtenderClick("ID-REPORTE-$index")
                        }
                    )
                }
            }
        }
    }
}