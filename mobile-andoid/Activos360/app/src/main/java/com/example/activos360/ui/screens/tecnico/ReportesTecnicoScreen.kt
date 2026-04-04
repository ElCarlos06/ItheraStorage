package com.example.activos360.ui.screens.tecnico

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.HeaderRegresar
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.activos360.ui.components.CaracteristicasSeccion
import com.example.activos360.ui.components.FilaPrioridad
import com.example.activos360.ui.components.FilaTipoFalla
import com.example.activos360.ui.components.InfoCard
import com.example.activos360.ui.components.MainAssetCard
import com.example.activos360.ui.components.MoonIcon
import com.example.activos360.ui.components.MoonIcons
import com.example.activos360.ui.components.SeccionDescripcion
import com.example.activos360.ui.components.SeccionEvidencia
import com.example.activos360.ui.components.SeccionUsuarioReporte

@Composable
fun ReportesTecnicoScreen(onBack: () -> Unit) {
    // 1. Estado para saber qué pestaña está seleccionada
    // 0 = Información, 1 = Reporte
    var tabSeleccionada  by  remember { mutableStateOf(0) }

    Scaffold (
        bottomBar = {
            // El botón de Atender solo se muestra si estamos en la pestaña 0 (Información)
            if (tabSeleccionada == 0) {
                Box(modifier = Modifier.padding(24.dp)) {
                    Buttons(text = "Atender", onClick = { /* TODO */ })
                }
            }
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color.White)
        ) {
            // 2. Header Azul
            HeaderRegresar(titulo = "Detalles del\nactivo", onBackClick = onBack)

            Spacer(modifier = Modifier.height(24.dp))

            // 3. SELECTOR DE PESTAÑAS (Las Píldoras)
            SelectorPestañas(
                seleccionada = tabSeleccionada,
                onTabSelected = { tabSeleccionada = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. CONTENIDO DINÁMICO
            if (tabSeleccionada == 0) {
                // Aquí va lo que ya tenemos: MainAssetCard, InfoCard, etc.
                ContenidoDetallesActivo()

            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        // Quitamos el padding horizontal de aquí para que sea igual a la otra pestaña
                        .padding(top = 8.dp)
                ) {
                    // Envolvemos todo en un padding consistente de 24dp
                    // SOLO si tus componentes no lo traen ya por dentro.
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                        MainAssetCard(
                            id = "#M-2024-1523",
                            nombre = "MacBook Pro 16\""
                        )

                        // Espacio entre la tarjeta y la primera fila
                        Spacer(modifier = Modifier.height(16.dp))

                        FilaTipoFalla(titulo = "Tipo de falla", valor = "Eléctrica")
                        FilaPrioridad(prioridad = "ALTA")
                        SeccionUsuarioReporte(nombreUsuario = "Dan")
                        SeccionDescripcion(cuerpo = "La laptop no enciende...")
                        SeccionEvidencia()

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SelectorPestañas(seleccionada: Int, onTabSelected: (Int) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(56.dp), // Un poquito más alto para que se vea más moderno
        shape = RoundedCornerShape(50.dp), // <--- ESTO HACE LA BARRA GRIS REDONDA
        color = Color(0xFFF8F9FF)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp), // ESTE PADDING es clave para que la píldora azul "flote" dentro
            verticalAlignment = Alignment.CenterVertically
        ) {
            PestañaItem(
                titulo = "Información",
                estaActiva = seleccionada == 0,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(0) }
            )
            PestañaItem(
                titulo = "Reporte",
                estaActiva = seleccionada == 1,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(1) }
            )
        }
    }
}

@Composable
fun PestañaItem(titulo: String, estaActiva: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(50.dp), // <--- ESTO HACE EL BOTÓN AZUL REDONDO
        color = if (estaActiva) Color(0xFF7B88FF) else Color.Transparent,
        contentColor = if (estaActiva) Color.White else Color.Gray
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = titulo,
                fontWeight = if (estaActiva) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

//Detalles de los cativos, aqui carlos usa su magia para conectar


@Composable
fun ContenidoDetallesActivo() {
    // Usamos una Column con scroll por si la info es mucha en pantallas chicas
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp), // Un pequeño respiro arriba
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Tarjeta Principal (Reutilizando tu componente)
        MainAssetCard(
            id = "ACTIVO #0482",
            nombre = "MacBook Pro 16\""
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. InfoCard de Marca (Reutilizando tu componente)
        InfoCard(
            imageVector = Icons.Outlined.BookmarkBorder,
            label = "Marca",
            value = "Apple"
        )

        // 3. InfoCard de Modelo
        InfoCard(
            imageVector = Icons.Outlined.GridView,
            label = "Modelo",
            value = "Pro 16"
        )

        // 4. Sección de Características
        CaracteristicasSeccion(
            lista = listOf(
                "Característica 1",
                "Característica 2",
                "Característica 3"
            )
        )

        // Espacio final para que el botón de "Atender" no tape la última info
        Spacer(modifier = Modifier.height(100.dp))
    }
}



@Preview(showSystemUi = true, name = "Vista Detalle Técnico Real")
@Composable
fun DetalleReporteTecnicoPreview() {
    // LLAMA DIRECTAMENTE A TU PANTALLA REAL
    ReportesTecnicoScreen(onBack = { })
}