package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.back.model.AuthDTO
import android.util.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URLEncoder

class LoginViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var loggedRole by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    var usuarioLogueadoCorreo by mutableStateOf("")
    var usuarioLogueadoNombre by mutableStateOf("Usuario")

    // Estado para manejar la navegación desde la UI
    private val _navegacionDestino = MutableStateFlow<String?>(null)
    val navegacionDestino: StateFlow<String?> = _navegacionDestino

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
                val response = ApiProvider.authApi.login(request)

                // Para 2xx: body() viene; para 4xx/5xx: parseamos errorBody como JSON de ApiResponse
                val apiResponse = if (response.isSuccessful) {
                    response.body()
                } else {
                    val raw = response.errorBody()?.string()
                    raw?.let { ApiProvider.parseModelApiResponse(it) }
                }

                if (apiResponse == null) {
                    errorMessage = "Error de servidor (${response.code()})"
                    return@launch
                }

                // Caso 1: primer login bloqueado (403) → navegar a crear contraseña (modo correo+temp)
                val dataMap = apiResponse.data as? Map<*, *>
                val requiresChange = (dataMap?.get("requiresPasswordChange") as? Boolean) == true
                val correo = dataMap?.get("correo") as? String
                if (requiresChange && !correo.isNullOrBlank()) {
                    _navegacionDestino.value = "create_password?correo=${URLEncoder.encode(correo, "UTF-8")}"
                    return@launch
                }

                // Caso 2: login normal → token en data.token
                val token = (dataMap?.get("token") as? String)
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
                    errorMessage = apiResponse.message ?: "Credenciales incorrectas"
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