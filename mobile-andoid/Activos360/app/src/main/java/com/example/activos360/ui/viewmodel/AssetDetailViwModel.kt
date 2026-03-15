package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// se define una clase AssetUIState que contiene un estado para los datos del Activo
data class AssetUIState(
    val nombre: String = "",
    val idEtiqueta: String = "",
    val isVisible: Boolean = false,
    val isLoading: Boolean = false
)

class AssetDetailViewModel : ViewModel() {

    // aqui se usa el  State de Compose para que la UI reaccione automáticamente cuando sacnees el QR o algo así
    var uiState by mutableStateOf(AssetUIState())
        private set

    // función que se dispara cuando el QR detecta algo
    fun onAssetScanned(etiqueta: String) {
        // aqui nomas simula que trae algo we, aqui es donde se conecta a la BD y busca el activo
        uiState = uiState.copy(isLoading = true)

        // Simulando que buscamos en la BD el código: $Etiqueta_producto
        // Esto es un ejemplo, luego vendrá de tu repositorio o del model ahi si no c 
        if (etiqueta.isNotEmpty()) {
            uiState = AssetUIState(
                nombre = "MacBook Pro 16\"",
                idEtiqueta = etiqueta,
                isVisible = true, // Abrimos el modal
                isLoading = false
            )
        }
    }

    // 4. Función para cerrar el modal
    fun dismissModal() {
        uiState = uiState.copy(isVisible = true)
    }


    // Agrega esto a tu AssetDetailViewModel o crea ResguardosViewModel
    var isResguardosModalVisible by mutableStateOf(false)
        private set

    fun showResguardos() { isResguardosModalVisible = true }
    fun dismissResguardos() { isResguardosModalVisible = false }

    fun confirmarResguardo() {
        // 1. Cerramos el modal
        isResguardosModalVisible = false
        // 2. Aquí podrías disparar un evento para que la UI se mueva al escáner
        // o simplemente cerrar el modal y que el usuario ya esté viendo la cámara.
    }



}

