package com.example.activos360.core.repository

import android.util.Base64
import com.example.activos360.back.model.AuthDTO
import com.example.activos360.back.model.ChangePasswordDTO
import com.example.activos360.back.model.RequestPasswordResetDTO
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.network.ApiProvider
import org.json.JSONObject

sealed class LoginResult {
    data class Success(val token: String, val role: String, val destino: String) : LoginResult()
    data class RequiresPasswordChange(val correo: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

object AuthRepository {

    suspend fun login(email: String, password: String): LoginResult {
        return try {
            val response = ApiProvider.authApi.login(AuthDTO(correo = email, password = password))

            val apiResponse = if (response.isSuccessful) {
                response.body()
            } else {
                response.errorBody()?.string()?.let { ApiProvider.parseModelApiResponse(it) }
            } ?: return LoginResult.Error("Error de servidor (${response.code()})")

            val dataMap = apiResponse.data as? Map<*, *>

            // Caso 1: primer login → requiere cambio de contraseña
            val requiresChange = (dataMap?.get("requiresPasswordChange") as? Boolean) == true
            val correo = dataMap?.get("correo") as? String
            if (requiresChange && !correo.isNullOrBlank()) {
                return LoginResult.RequiresPasswordChange(correo)
            }

            // Caso 2: login normal
            val token = dataMap?.get("token") as? String
            if (!token.isNullOrBlank()) {
                TokenManager.saveToken(token)
                val role = extraerRolDelToken(token)
                    ?: return LoginResult.Error("Login exitoso, pero no se pudo extraer el rol.")
                val destino = when (role.uppercase()) {
                    "EMPLEADO" -> "home_empleado"
                    "TECNICO"  -> "home_tecnico"
                    else       -> return LoginResult.Error("Rol desconocido: $role")
                }
                return LoginResult.Success(token, role, destino)
            }

            LoginResult.Error(apiResponse.message ?: "Credenciales incorrectas")
        } catch (e: Exception) {
            LoginResult.Error("Error de conexión: ${e.localizedMessage}")
        }
    }

    suspend fun changePassword(dto: ChangePasswordDTO): Result<String> = try {
        val resp = ApiProvider.authApi.changePassword(dto)
        val body = resp.body()
        if (resp.isSuccessful && body?.error == false) {
            Result.success(body.message ?: "Contraseña actualizada")
        } else {
            Result.failure(Exception(body?.message
                ?: "No se pudo actualizar la contraseña (${resp.code()})"))
        }
    } catch (e: Exception) { Result.failure(e) }

    suspend fun requestPasswordReset(correo: String): Result<String> = try {
        val resp = ApiProvider.authApi.requestPasswordReset(
            RequestPasswordResetDTO(correo = correo.trim())
        )
        val body = resp.body()
        if (resp.isSuccessful && body?.error == false) {
            Result.success(body.message ?: "Revisa tu correo para restablecer tu contraseña")
        } else {
            Result.failure(Exception(body?.message
                ?: "No se pudo enviar el correo (${resp.code()})"))
        }
    } catch (e: Exception) { Result.failure(e) }

    fun extraerRolDelToken(token: String): String? = try {
        val partes = token.split(".")
        if (partes.size == 3) {
            val bytes = Base64.decode(partes[1], Base64.URL_SAFE)
            val json  = JSONObject(String(bytes, Charsets.UTF_8))
            json.optString("role", null) ?: json.optString("rol", null)
        } else null
    } catch (_: Exception) { null }
}
