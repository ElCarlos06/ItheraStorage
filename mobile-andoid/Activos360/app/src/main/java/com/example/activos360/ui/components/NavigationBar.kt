package com.example.activos360.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// --- NUEVOS IMPORTS NECESARIOS ---
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
@Composable
fun BottomCustomBar(
    navController: NavController,
    onQrScanned: (String) -> Unit = {}
) { // 1. Recibimos el controlador

    // 2. Leemos en qué pantalla estamos actualmente
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Definimos los colores para reutilizarlos
    val colorActivo = Color(0xFF8B93FF)
    val colorInactivo = Color.Gray

    val context = LocalContext.current
    // remember evita que GmsBarcodeScanning.getClient() se llame en cada recomposición
    val scanner = remember(context) {
        val scannerOptions = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
        GmsBarcodeScanning.getClient(context, scannerOptions)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. Barra blanca de fondo
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // --- BOTÓN HOME (Apunta al Scanner) ---
                val isHomeSelected = currentRoute == "scanner" // ¿Estamos en home?

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // 3. Agregamos la navegación
                            navController.navigate("scanner") {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                ) {
                    // Indicador (solo se muestra si está seleccionado)
                    Box(
                        modifier = Modifier
                            .width(35.dp)
                            .height(4.dp)
                            .background(
                                color = if (isHomeSelected) colorActivo else Color.Transparent,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MoonIcon(
                        icon = MoonIcons.GenericHome,
                        contentDescription = "Home",
                        tint = if (isHomeSelected) colorActivo else colorInactivo,
                        size = 28.dp
                    )
                }

                Spacer(modifier = Modifier.width(80.dp))

                // --- BOTÓN PERFIL ---
                val isPerfilSelected = currentRoute == "perfil" // ¿Estamos en perfil?

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // 3. Agregamos la navegación
                            navController.navigate("perfil") {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                ) {
                    // Espacio o indicador
                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(35.dp)
                            .background(
                                color = if (isPerfilSelected) colorActivo else Color.Transparent,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    MoonIcon(
                        icon = MoonIcons.GenericUser,
                        contentDescription = "Perfil",
                        tint = if (isPerfilSelected) colorActivo else colorInactivo,
                        size = 28.dp
                    )
                }
            }
        }

        // 2. El Botón Central Flotante (Scanner)
        Surface(
            modifier = Modifier
                .size(75.dp)
                .align(Alignment.TopCenter)
                .offset(y = 5.dp)
                .clickable {
                    scanner.startScan()
                        .addOnSuccessListener { barcode ->
                            // Cuando escanea con éxito, sacamos el texto del QR
                            val qrResult = barcode.rawValue ?: ""
                            onQrScanned(qrResult)
                            Toast.makeText(context, "QR Escaneado: $qrResult", Toast.LENGTH_LONG).show()

                            // OPCIONAL: Aquí podrías navegar a otra pantalla pasando el QR
                            // navController.navigate("detalle_activo/$qrResult")
                        }
                        .addOnCanceledListener {
                            // Si el usuario cierra la cámara sin escanear nada
                            Toast.makeText(context, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Si ocurre un error
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                },
            shape = CircleShape,
            color = colorActivo,
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                MoonIcon(
                    icon = MoonIcons.SecurityQrCode,
                    contentDescription = "Scan",
                    tint = Color.White,
                    size = 35.dp
                )
            }
        }
    }
}

// --- ACTUALIZAMOS EL PREVIEW PARA QUE NO MARQUE ERROR ---
@Composable
@Preview
fun previwnav() {
    // Usamos un NavController falso (dummy) solo para que el preview pueda compilar
    val dummyNavController = rememberNavController()
    BottomCustomBar(navController = dummyNavController)
}