@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.activos360.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CardBg = Color(0xFFF8F9FE)
private val TextDark = Color(0xFF2D3436)

@Composable
fun DropdownSelector(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        FieldLabel(label)
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder, color = TextDark, fontSize = 16.sp) },
                trailingIcon = {
                    MoonIcon(icon = MoonIcons.ControlsChevronDown, contentDescription = null, tint = TextDark, size = 20.dp)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = CardBg,
                    focusedContainerColor = CardBg,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF7B88FF)
                ),
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 16.sp, color = TextDark)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Color.White
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
