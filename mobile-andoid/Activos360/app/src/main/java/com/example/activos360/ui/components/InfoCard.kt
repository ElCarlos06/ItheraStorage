package com.example.activos360.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentColor = Color(0xFF7B88FF)

@Composable
fun InfoCard(
    icon: Int? = null,
    imageVector: ImageVector? = null,
    label: String,
    value: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF8F9FF)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            imageVector != null -> Icon(
                                imageVector = imageVector,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = AccentColor
                            )
                            icon != null -> MoonIcon(
                                icon = icon,
                                contentDescription = null,
                                size = 22.dp,
                                tint = AccentColor
                            )
                        }
                    }
                }

                Text(
                    text = label,
                    modifier = Modifier.padding(start = 16.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436)
                )
            }

            Text(
                text = value,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CaracteristicasSeccion(lista: List<String>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF8F9FF)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FormatListBulleted,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = Color(0xFF7B88FF)
                        )
                    }
                }
                Text(
                    text = "Características",
                    modifier = Modifier.padding(start = 16.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            lista.forEach { item ->
                Text(
                    text = item,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 56.dp, bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MainAssetCard(id: String, nombre: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F2F6)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFF1F2F6),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    MoonIcon(
                        icon = MoonIcons.DevicesMacbook,
                        contentDescription = null,
                        size = 28.dp,
                        tint = Color(0xFF6B7280)
                    )
                }
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = id,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InfoCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 20.dp)
    ) {
        // Ejemplo de Marca
        InfoCard(
            imageVector = Icons.Outlined.BookmarkBorder,
            label = "Marca",
            value = "Apple"
        )

        InfoCard(
            imageVector = Icons.Outlined.GridView,
            label = "Modelo",
            value = "Pro 16"
        )
    }
}