package com.example.activos360.ui.modals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.ui.viewmodel.AssetDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailModal(
    onDismiss: () -> Unit,
    nombreActivo: String = "MacBook Pro 16\"",
    idActivo: String = "IT-4589"
) {
    val viewModel: AssetDetailViewModel = viewModel()
    val state = viewModel.uiState
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        // Bordes superiores muy redondeados como en el diseño
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            // La rayita gris de arriba para deslizar
            androidx.compose.material3.BottomSheetDefaults.DragHandle(
                color = Color.LightGray.copy(alpha = 0.5f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Nombre del producto
            Text(
                text = nombreActivo,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ID / Etiqueta
            Text(
                text = "ID: $idActivo",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón principal
            Button(
                onClick = { /* Acción para ver detalles */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B93FF) // Tu azul/morado de acento
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Ver detalles",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (state.isVisible) {
        AssetDetailModal(
            nombreActivo = state.nombre,
            idActivo = state.idEtiqueta,
            onDismiss = { viewModel.dismissModal() }
        )
    }
}

@Composable
@Preview
fun VistaModal(){
    AssetDetailModal(onDismiss = {})
}
