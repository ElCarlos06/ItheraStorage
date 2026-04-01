package com.example.activos360.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.network.ApiProvider
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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
        rolUsuario = TokenManager.getRoleFromToken()
            ?.removePrefix("ROLE_") ?: "Empleado"
        correoUsuario = TokenManager.getCorreoFromToken() ?: ""
        cargarFoto()
    }

    fun cargarFoto() {
        val correo = correoUsuario
        if (correo.isBlank()) return
        viewModelScope.launch {
            try {
                val resp = ApiProvider.imagenPerfilApi.obtenerImagen(correo)
                if (resp.isSuccessful) {
                    fotoUsuario = urlFromData(resp.body()?.data)
                }
            } catch (_: Exception) { }
        }
    }

    fun subirFoto(uri: Uri, context: Context) {
        val correo = correoUsuario
        if (correo.isBlank()) return
        viewModelScope.launch {
            isUploadingPhoto = true
            try {
                val stream = context.contentResolver.openInputStream(uri) ?: return@launch
                val bytes = stream.readBytes()
                stream.close()
                val requestBody = bytes.toRequestBody("image/*".toMediaType())
                val part = MultipartBody.Part.createFormData("file", "perfil.jpg", requestBody)
                val resp = ApiProvider.imagenPerfilApi.subirImagen(correo, part)
                if (resp.isSuccessful) {
                    // Intentar obtener la URL directamente de la respuesta del POST
                    val urlFromPost = urlFromData(resp.body()?.data)
                    if (!urlFromPost.isNullOrBlank()) {
                        fotoUsuario = urlFromPost
                    } else {
                        cargarFoto() // fallback: GET
                    }
                }
            } catch (_: Exception) {
            } finally {
                isUploadingPhoto = false
            }
        }
    }

    /** Extrae la URL de la imagen desde el campo `data` de la respuesta (Map o String). */
    private fun urlFromData(data: Any?): String? {
        return when (data) {
            is Map<*, *> -> data["url"] as? String
            is String    -> data.takeIf { it.startsWith("http") }
            else         -> null
        }
    }

    /** Llamado desde LoginScreen tras un login exitoso para sincronizar datos. */
    fun cargarDatosUsuario(nombre: String, rol: String, correo: String = "", foto: String? = null) {
        nombreUsuario = nombre
        rolUsuario = rol
        fotoUsuario = foto
        if (correo.isNotBlank() && correo != correoUsuario) {
            correoUsuario = correo
            cargarFoto()   // correo recién disponible → cargar foto de perfil
        }
    }

    fun limpiarDatos() {
        nombreUsuario = "Cargando..."
        rolUsuario = "Empleado"
        correoUsuario = ""
        fotoUsuario = null
    }
}
