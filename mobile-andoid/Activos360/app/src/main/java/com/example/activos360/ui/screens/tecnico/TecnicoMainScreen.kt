package com.example.activos360.ui.screens.tecnico

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.util.Log
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.core.util.QrParse
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string
import com.example.activos360.ui.components.BottomCustomBar
import com.example.activos360.ui.modals.AssetDetailModal
import com.example.activos360.ui.screens.Empleado.TecnicoHome
import kotlinx.coroutines.launch

@Composable
fun TecnicoMainScreen(navControllerPrincipal: NavController) {
    Log.d("TECNICO_SCAN", "=== TecnicoMainScreen COMPUESTO ===")
    val bottomNavController = rememberNavController()
    val scope = rememberCoroutineScope()

    var showModal by remember { mutableStateOf(false) }
    var codigoEscaneado by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomCustomBar(
                navController = bottomNavController,
                onQrScanned = { codigo ->
                    val activoId = QrParse.extractActivoId(codigo) ?: 0L
                    scope.launch {
                        var navegoDirecto = false
                        try {
                            val userId = TokenManager.getUserIdFromToken()
                            Log.d("TECNICO_SCAN", "activoId=$activoId userId=$userId")

                            if (activoId > 0L) {
                                val resp = ApiProvider.mantenimientoApi.findByActivo2(activoId)
                                Log.d("TECNICO_SCAN", "HTTP ${resp.code()}, bodyNull=${resp.body() == null}")

                                if (resp.isSuccessful) {
                                    // El backend puede devolver lista directa O paginado {content:[...]}
                                    @Suppress("UNCHECKED_CAST")
                                    val rawData = resp.body()?.data
                                    Log.d("TECNICO_SCAN", "data type=${rawData?.javaClass?.simpleName}, value=$rawData")

                                    val list: List<Map<String, Any?>> = when (rawData) {
                                        is List<*> -> rawData.filterIsInstance<Map<String, Any?>>()
                                        is Map<*, *> -> (rawData["content"] as? List<*>)
                                            ?.filterIsInstance<Map<String, Any?>>() ?: emptyList()
                                        else -> emptyList()
                                    }
                                    Log.d("TECNICO_SCAN", "mantenimientos encontrados: ${list.size}")

                                    val miMantenimiento = list.firstOrNull { m ->
                                        val tecnicoId = (m["usuarioTecnico"].asMap())?.long("id")
                                            ?: m.long("idUsuarioTecnico")
                                        val estado = m.string("estadoMantenimiento")?.lowercase() ?: ""
                                        Log.d("TECNICO_SCAN", "  mtn id=${m.entries.find { it.key == "id" }?.value} tecnicoId=$tecnicoId estado=$estado")
                                        // Si no tenemos userId del token, comparamos solo si encontramos exactamente 1 mtn activo
                                        val estadoActivo = estado !in listOf("completado", "cerrado")
                                        if (userId != null) tecnicoId == userId && estadoActivo
                                        else estadoActivo
                                    }

                                    if (miMantenimiento != null) {
                                        val mantenimientoId = miMantenimiento.long("id") ?: 0L
                                        Log.d("TECNICO_SCAN", "Navegando a reporte_tecnico/$activoId/$mantenimientoId")
                                        navControllerPrincipal.navigate("reporte_tecnico/$activoId/$mantenimientoId")
                                        navegoDirecto = true
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("TECNICO_SCAN", "Error: ${e.message}", e)
                        }

                        if (!navegoDirecto) {
                            codigoEscaneado = codigo
                            showModal = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = "scanner",
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = 0.dp
            )
        ) {
            composable("scanner") {
                TecnicoHome()
            }
            composable("perfil") {
                UserProfile()
            }
        }
    }

    if (showModal) {
        AssetDetailModal(
            idActivo = codigoEscaneado,
            onDismiss = { showModal = false },
            onVerDetallesClick = {
                showModal = false
                val id = QrParse.extractActivoId(codigoEscaneado) ?: 0L
                navControllerPrincipal.navigate("detalles_activo/$id")
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun TecnicoMainScreenPreview() {
    val fakeNavController = rememberNavController()
    TecnicoMainScreen(navControllerPrincipal = fakeNavController)
}
