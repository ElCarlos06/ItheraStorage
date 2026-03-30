package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.example.activos360.back.api.ImagenPerfilControllerApi
import com.example.activos360.core.auth.TokenManager
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

    init {
        nombreUsuario = TokenManager.getNombreFromToken() ?: "Usuario"
        rolUsuario = TokenManager.getRoleFromToken()
            ?.removePrefix("ROLE_") ?: "Empleado"
        correoUsuario = TokenManager.getCorreoFromToken() ?: ""
    }

    fun cargarDatosUsuario(nombre: String, rol: String, correo: String = "", foto: String? = null) {
        nombreUsuario = nombre
        rolUsuario = rol
        correoUsuario = correo
        fotoUsuario = foto
    }

    // 2. Llama a esta función cuando el usuario presione "Cerrar sesión"
    fun limpiarDatos() {
        nombreUsuario = "Cargando..."
        rolUsuario = "Empleado"
        correoUsuario = ""
        fotoUsuario = null
    }
/*
    fun obtenerFotoPerfil(correo: String, api: ImagenPerfilControllerApi) {
        viewModelScope.launch {
            try {
                // 1. Hacemos la petición a la red
                val response = api.findByCorreo(correo)

                // 2. Si la petición fue exitosa (código 200)
                if (response.isSuccessful) {
                    val body = response.body()

                    // 3. Verificamos que el backend no haya reportado error y traiga data
                    if (body != null && !body.error && body.data != null) {
                        // ¡Extraemos la URL y actualizamos la variable de Compose!
                        fotoUsuario = body.data.url
                    } else {
                        // El backend respondió OK, pero quizás el usuario no tiene foto aún
                        fotoUsuario = null
                    }
                } else {
                    // Hubo un error HTTP (ej. 404, 500)
                    fotoUsuario = null
                }
            } catch (e: Exception) {
                // Error de red (sin internet) o error de conversión JSON
                e.printStackTrace()
                fotoUsuario = null
            }
        }
    }*/
}