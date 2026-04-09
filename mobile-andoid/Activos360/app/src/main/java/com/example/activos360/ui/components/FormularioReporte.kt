package com.example.activos360.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFormulario(
    label: String,
    placeholder: String,
    minLines: Int = 1
) {
    Column (modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF2D3436),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFB2BEC3),
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            // ESTA ES LA PARTE QUE DA ERROR, USA ESTA SINTAXIS:
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF8F9FE),
                unfocusedContainerColor = Color(0xFFF8F9FE),
                disabledContainerColor = Color(0xFFF8F9FE),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            minLines = minLines
        )
    }
}

