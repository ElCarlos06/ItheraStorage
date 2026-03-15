package com.example.activos360.ui.modals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.viewmodel.AssetDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailModal(
    nombreActivo: String,
    idActivo: String,
    onDismiss: () -> Unit,
    onVerDetallesClick: () -> Unit // Nueva acción para el botón
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
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

            Text(
                text = nombreActivo,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ID: $idActivo",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- AQUÍ USAMOS TU COMPONENTE REUTILIZABLE ---
            Buttons(
                text = "Ver detalles",
                onClick = onVerDetallesClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VistaModalPreview() {
    // Definimos qué queremos ver en el diseño
    AssetDetailModal(
        nombreActivo = "MacBook Pro 16\"",
        idActivo = "IT-4589",
        onDismiss = { /* No hace nada en preview */ },
        onVerDetallesClick = { /* No hace nada en preview */ }
    )


}