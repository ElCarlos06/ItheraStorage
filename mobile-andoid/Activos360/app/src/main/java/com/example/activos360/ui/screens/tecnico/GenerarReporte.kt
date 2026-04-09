package com.example.activos360.ui.screens.tecnico

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.CampoFormulario
import com.example.activos360.ui.components.EvidenciasSection
import com.example.activos360.ui.components.HeaderRegresar
import com.example.activos360.ui.components.MainAssetCard
import com.example.activos360.ui.modals.ModalMantenimientoIncompleto


@Composable
fun AtenderMantenimientoScreen() {
    var mostrarModal by remember { mutableStateOf(false) }
    val fotosSeleccionadas = remember { mutableStateListOf<Uri>() }

    if (mostrarModal) {
        ModalMantenimientoIncompleto(
            onDismiss = { mostrarModal = false },
            onConfirm = {
                mostrarModal = false
               /* aqui va lo de regresar a el formulario o cerrar el modal
               * peroo no c como lo estes manejando si aqui en la
               *  vista o en un view model*/
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState ())
    ) {
        // 1. Header Azul con Curva (El que ya tienes de "Atender mantenimiento")
        HeaderRegresar(titulo = "Atender \nMantenimiento",
            onBackClick = { mostrarModal = true })


        MainAssetCard(id = "ACTIVO #0482", nombre = "MacBook Pro 16\"")

        Column(
            modifier = Modifier.padding(
                horizontal = 24.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
            )
        {
            // 2. Tarjeta del Activo (Reutilizamos la que ya tienes)
            Spacer(modifier = Modifier.height(24.dp))


            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                // 3. Los Campos del Formulario
                CampoFormulario(
                    label = "Diagnóstico técnico",
                    placeholder = "Escriba el diagnóstico detallado..",
                    minLines = 4
                )

                CampoFormulario(
                    label = "Acciones realizadas",
                    placeholder = "Detalla las reparaciones efectuadas..",
                    minLines = 4
                )

                CampoFormulario(
                    label = "Piezas utilizadas",
                    placeholder = "Lista de repuestos..",
                    minLines = 3
                )

                // 4. Sección de Evidencias (Fotos)
                Spacer(modifier = Modifier.height(16.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                }

                Spacer(modifier = Modifier.height(12.dp))


                EvidenciasSection(
                    fotos = fotosSeleccionadas,
                    maxFotos = 3,
                    onAddClick = {
                        // Aquí dispararías el selector de fotos (File Picker)
                        // Por ahora, para probar, podrías agregar una URI de ejemplo
                        println("Abrir cámara o galería")
                    },
                    onRemove = { index ->
                        // Quitamos la foto de la lista por su posición
                        fotosSeleccionadas.removeAt(index)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón finalizar...
            }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 5. Botón de Resultado o Finalizar (Si lo lleva abajo)
                // ...

            Buttons(
                text = "Activo Atendido"
            ) { }
            Spacer(modifier = Modifier.height(32.dp))
            }




        }

    }



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AtenderMantenimientoPreview() {
    // Llamamos directamente a tu pantalla principal
    AtenderMantenimientoScreen()
}