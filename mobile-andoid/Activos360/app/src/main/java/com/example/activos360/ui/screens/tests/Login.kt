package com.example.activos360.ui.screens.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.WaveHeader
import com.example.activos360.ui.viewmodel.LoginViewModel

@Composable
fun LoginTest(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToHome: () -> Unit // <-- 1. Agregamos esto
) {
    // Estados para los textos
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // 2. Escuchamos el estado de éxito del ViewModel
    LaunchedEffect(viewModel.loginSuccess) {
        if (viewModel.loginSuccess) {
            onNavigateToHome() // Dispara la navegación
        }
    }

    //COLOR DE LA OLA, HAY Q ESTABLECERLO COMO PRIMARY PARA DESPUES
    val primaryColor = Color(0xFF7B88FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LLAMAR A LA FUNCIONM PARA PONERLA HASTA ARRIBA
        WaveHeader(color = primaryColor)

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Sección del Logo y Título
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Hay q METER EL LOGO AQUI ENTONCES Y QUITAR EL TEXTO SI TODO VA A SER LOGO
            // Image(painter = painterResource(id = R.drawable.tu_logo), contentDescription = "Logo")
            Text(
                text = "Activos 360",
                color = primaryColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 3. Formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // --- Input Email ---
            Text(
                text = "Email",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Ingresa tú email", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Input Contraseña ---
            Text(
                text = "Contraseña",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Ingresa la contraseña", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = primaryColor)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- ¿Olvidaste tu contraseña? ---
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = primaryColor,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { /* TODO: Acción de recuperar password */ }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- MOSTRAR ERROR SI LAS CREDENCIALES FALLAN ---
            viewModel.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp)
                )
            }

            // --- Botón Iniciar Sesión  HAY QUE TERMINAR EL COMPONENTE---
            Button(
                onClick = { viewModel.performLogin(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = !viewModel.isLoading // Se desactiva para evitar múltiples clics
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}