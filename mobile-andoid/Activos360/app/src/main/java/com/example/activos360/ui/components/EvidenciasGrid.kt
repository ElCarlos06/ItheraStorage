package com.example.activos360.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentColor = Color(0xFF8B96EB)

@Composable
fun EvidenciasGrid(
    fotos: List<Uri>,
    maxFotos: Int = 3,
    onAddClick: () -> Unit,
    onRemove: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FieldLabel("Evidencias")
            Text(
                text = "Max $maxFotos fotos",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AccentColor
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        val items = mutableListOf<@Composable () -> Unit>()
        if (fotos.size < maxFotos) {
            items.add { GridAddButton(onClick = onAddClick) }
        }
        fotos.forEachIndexed { index, uri ->
            items.add { GridFotoThumbnail(uri = uri, onRemove = { onRemove(index) }) }
        }

        val rows = items.chunked(2)
        rows.forEachIndexed { rowIndex, rowItems ->
            if (rowIndex > 0) Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) { item() }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun GridAddButton(onClick: () -> Unit) {
    val dashedColor = AccentColor.copy(alpha = 0.4f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .drawBehind {
                drawRoundRect(
                    color = dashedColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            }
            .background(AccentColor.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        MoonIcon(
            icon = MoonIcons.FilesSave,
            contentDescription = "Agregar foto",
            size = 32.dp,
            tint = AccentColor
        )
    }
}

@Composable
private fun GridFotoThumbnail(uri: Uri, onRemove: () -> Unit) {
    val context = LocalContext.current
    val bitmap = remember(uri) {
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } catch (_: Exception) { null }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF3F4F6)
            ) {}
        }
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    MoonIcon(
                        icon = MoonIcons.ControlsClose,
                        contentDescription = "Eliminar",
                        tint = Color.White,
                        size = 14.dp
                    )
                }
            }
        }
    }
}
