package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.core.auth.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.activos360.back.api.AuthControllerApi
import com.example.activos360.back.model.AuthDTO

import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import android.util.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

class LoginViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var loggedRole by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    // Estado para manejar la navegación desde la UI
    private val _navegacionDestino = MutableStateFlow<String?>(null)
    val navegacionDestino: StateFlow<String?> = _navegacionDestino

    // 1. CREAMOS EL ADAPTADOR DE MOSHI PARA KOTLIN
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Configuración con MoshiConverterFactory
    // 2. SE LO PASAMOS A RETROFIT
    private val retrofit = Retrofit.Builder()
        //.baseUrl("http://10.0.2.2:8080/") // IP para el emulador
        .baseUrl("http://192.168.0.82:8080/") // IP para el real
        .addConverterFactory(MoshiConverterFactory.create(moshi)) // <-- Ahora sí funcionará
        .build()

    private val api = retrofit.create(AuthControllerApi::class.java)

    fun performLogin(email: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // Creamos el objeto con los datos de la UI
                val request = AuthDTO(
                    correo = email,
                    password = pass
                )
                // Llamada a tu interfaz AuthControllerApi
                val response = api.login(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.data?.token
                    if (!token.isNullOrBlank()) {
                        TokenManager.saveToken(token)
                        val rolDelBackend = extraerRolDelToken(token)
                        if (rolDelBackend != null) {
                            loggedRole = rolDelBackend
                            when (rolDelBackend.uppercase()) {
                                "EMPLEADO" -> _navegacionDestino.value = "home_empleado"
                                "TECNICO" -> _navegacionDestino.value = "home_admin"
                                else -> errorMessage = "Rol desconocido: $rolDelBackend"
                            }
                        } else {
                            errorMessage = "Login exitoso, pero no se pudo extraer el rol del token."
                        }
                    } else {
                        errorMessage = body?.message ?: "Login exitoso, pero no se encontró el token."
                    }
                } else {
                    // Si el servidor responde 401, 403, etc.
                    errorMessage = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                // Si el backend está apagado o no hay internet
                errorMessage = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // Función que limpia la navegación (la llamaremos desde la vista Login)
    fun navegacionCompletada() {
        _navegacionDestino.value = null
    }

    fun extraerRolDelToken(token: String): String? {
        return try {
            val partes = token.split(".")
            if (partes.size == 3) {
                val payloadBase64 = partes[1]
                // Los JWT usan Base64 URL Safe
                val bytesDecodificados = Base64.decode(payloadBase64, Base64.URL_SAFE)
                val jsonDecodificado = String(bytesDecodificados, Charsets.UTF_8)

                // Convertimos el string a un objeto JSON
                val jsonObject = JSONObject(jsonDecodificado)

                // Extraemos el rol (Asegúrate de que la clave "rol" sea exactamente la que usas en Spring Boot)
                jsonObject.optString("role", null) ?: jsonObject.optString("rol", null)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}