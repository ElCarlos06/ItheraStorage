package com.example.activos360.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CardBg = Color(0xFFF8F9FE)
private val TextDark = Color(0xFF2D3436)

private val ColorBaja = Color(0xFF2E7D32)
private val ColorMedia = Color(0xFFFFB319)
private val ColorAlta = Color(0xFFD3304A)

private fun chipColor(option: String): Color = when (option.lowercase()) {
    "baja" -> ColorBaja
    "media" -> ColorMedia
    "alta" -> ColorAlta
    else -> ColorMedia
}

@Composable
fun ChipSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column {
        FieldLabel(label)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { option ->
                val isSelected = selected == option
                val accent = chipColor(option)
                Surface(
                    onClick = { onSelect(option) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) accent.copy(alpha = 0.12f) else CardBg,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) accent else Color.Transparent
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) accent else TextDark
                        )
                    }
                }
            }
        }
    }
}
