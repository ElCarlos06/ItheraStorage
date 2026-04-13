package com.example.activos360.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.repository.PerfilRepository
import kotlinx.coroutines.launch

class EmpleadoViewModel : ViewModel() {

    var nombreUsuario by mutableStateOf("Cargando...")
        private set

    var rolUsuario by mutableStateOf("Empleado")
        private set

    var fotoUsuario by mutableStateOf<String?>(null)
        private set

    var correoUsuario by mutableStateOf("")
        private set

    var isUploadingPhoto by mutableStateOf(false)
        private set

    init {
        nombreUsuario = TokenManager.getNombreFromToken() ?: "Usuario"
        rolUsuario    = TokenManager.getRoleFromToken()?.removePrefix("ROLE_") ?: "Empleado"
        correoUsuario = TokenManager.getCorreoFromToken() ?: ""
        cargarFoto()
    }

    fun cargarFoto() {
        val correo = correoUsuario
        if (correo.isBlank()) return
        viewModelScope.launch {
            fotoUsuario = PerfilRepository.obtenerFotoUrl(correo)
        }
    }

    fun subirFoto(uri: Uri, context: Context) {
        val correo = correoUsuario
        if (correo.isBlank()) return
        viewModelScope.launch {
            isUploadingPhoto = true
            try {
                val url = PerfilRepository.subirFoto(correo, uri, context)
                fotoUsuario = url ?: run { cargarFoto(); fotoUsuario }
            } finally {
                isUploadingPhoto = false
            }
        }
    }

    fun cargarDatosUsuario(nombre: String, rol: String, correo: String = "", foto: String? = null) {
        nombreUsuario = nombre
        rolUsuario    = rol
        fotoUsuario   = foto
        if (correo.isNotBlank() && correo != correoUsuario) {
            correoUsuario = correo
            cargarFoto()
        }
    }

    fun limpiarDatos() {
        nombreUsuario = "Cargando..."
        rolUsuario    = "Empleado"
        correoUsuario = ""
        fotoUsuario   = null
    }
}
