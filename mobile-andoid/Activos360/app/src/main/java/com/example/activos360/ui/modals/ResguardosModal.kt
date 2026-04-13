package com.example.activos360.ui.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activos360.ui.components.MoonIcon
import com.example.activos360.ui.components.MoonIcons
import com.example.activos360.ui.viewmodel.ActivoBannerItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResguardosModal(
    items: List<ActivoBannerItem>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF8F9FF),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Resguardos Pendientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "${items.size} activo${if (items.size == 1) "" else "s"} esperando confirmación",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            if (items.isEmpty()) {
                Text(
                    text = "No hay resguardos pendientes.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    items(items) { item ->
                        ResguardoBannerItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun ResguardoBannerItemCard(item: ActivoBannerItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFF1F2F6),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                MoonIcon(
                    icon = MoonIcons.DevicesMacbook,
                    contentDescription = null,
                    size = 24.dp,
                    tint = Color.Gray,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = item.etiqueta,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = item.nombre,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3436)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.ubicacion,
                    fontSize = 12.sp,
                    color = Color(0xFF7B88FF)
                )
            }
        }
    }
}

@Composable
fun ReporteBannerItemCard(item: ActivoBannerItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFF1F2F6),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                MoonIcon(
                    icon = MoonIcons.DevicesMacbook,
                    contentDescription = null,
                    size = 24.dp,
                    tint = Color.Gray,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = item.etiqueta,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = item.nombre,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3436)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.ubicacion,
                    fontSize = 12.sp,
                    color = Color(0xFF7B88FF)
                )
            }
        }
    }
}

// Kept for backwards compatibility
@Composable
fun ResguardoItemCard(onConfirmClick: () -> Unit) {
    ResguardoBannerItemCard(
        item = ActivoBannerItem(0L, "MacBook Pro 16\"", "ACTIVO #0482", "Oficina 3")
    )
}

@Composable
fun ReporteItemCard(onConfirmClick: () -> Unit) {
    ReporteBannerItemCard(
        item = ActivoBannerItem(0L, "MacBook Pro 16\"", "ACTIVO #0482", "Oficina 3")
    )
}
