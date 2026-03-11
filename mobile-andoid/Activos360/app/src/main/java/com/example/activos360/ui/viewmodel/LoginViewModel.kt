package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory // ASEGÚRATE DE ESTE IMPORT
import com.example.activos360.api.AuthControllerApi
import com.example.activos360.models.AuthDTO

import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
class LoginViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var loginSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // 1. CREAMOS EL ADAPTADOR DE MOSHI PARA KOTLIN
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Configuración con MoshiConverterFactory
    // 2. SE LO PASAMOS A RETROFIT
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
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
                    // Si el servidor responde 200 OK
                    loginSuccess = true
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
}