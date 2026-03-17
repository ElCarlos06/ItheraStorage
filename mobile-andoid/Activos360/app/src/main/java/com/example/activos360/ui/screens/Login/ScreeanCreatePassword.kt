package com.example.activos360.ui.screens.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.activos360.R
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.MoonIcon
import com.example.activos360.ui.components.MoonIcons
import com.example.activos360.ui.viewmodel.CreatePasswordViewModel

@Composable
fun ScreeanCreatePassword(
    tokenFromLink: String? = null,
    correoFromFirstLogin: String? = null,
    onBackClick: () -> Unit = {},
    onPasswordUpdated: () -> Unit = {},
    viewModel: CreatePasswordViewModel = viewModel()
) {
    // Estados para los textos
    var token by remember { mutableStateOf(tokenFromLink.orEmpty()) }
    var correo by remember { mutableStateOf(correoFromFirstLogin.orEmpty()) }
    var passwordActual by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(tokenFromLink) {
        if (!tokenFromLink.isNullOrBlank()) token = tokenFromLink
    }

    LaunchedEffect(correoFromFirstLogin) {
        if (!correoFromFirstLogin.isNullOrBlank()) correo = correoFromFirstLogin
    }

    val isFirstLoginMode = !correoFromFirstLogin.isNullOrBlank()

    //COLOR DE LA OLA, HAY Q ESTABLECERLO COMO PRIMARY PARA DESPUES
    val primaryColor = Color(0xFF7B88FF)
    val secondaryColor = Color(0xFF000000)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LLAMAR A LA FUNCIONM PARA PONERLA HASTA ARRIBA


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {
            IconButton(onClick = onBackClick) {
                MoonIcon(icon = MoonIcons.ArrowsLeft, contentDescription = "Regresar")
            }

            // Imagen centrada
            Icon(
                painter = painterResource(R.drawable.targeta),
                contentDescription = "Targeta",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )
        }


        Spacer(modifier = Modifier.height(30.dp))

        // 2. Sección del Logo y Título
        Row(
            verticalAlignment = Alignment.CenterVertically

        ) {

            Text(
                text = "Restablecer contraseña",
                color = secondaryColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            if (!isFirstLoginMode) {
                Text(
                    text = "Token",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    placeholder = { Text("Pega aquí el token del enlace", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    enabled = !uiState.isLoading && tokenFromLink.isNullOrBlank()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (isFirstLoginMode) {
                Text(
                    text = "Correo",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    enabled = false
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Contraseña temporal",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = passwordActual,
                    onValueChange = { passwordActual = it },
                    placeholder = { Text("Escribe la contraseña temporal", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    enabled = !uiState.isLoading
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            //--Input Nueva contraseña--
            Text(
                text = "Nueva Contraseña",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = { Text("Nueva Contraseña", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) MoonIcons.ControlsEye else MoonIcons.ControlsEyeCrossed
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        MoonIcon(icon = icon, contentDescription = "Mostrar contraseña", tint = primaryColor)
                    }
                },
                singleLine = true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(10.dp))

            //--Input Confirmar contraseña--
            Text(
                text = "Confirmar Contraseña",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Confirmar Contraseña", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                enabled = !uiState.isLoading
            )

            uiState.errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = msg, color = Color(0xFFD33030))
            }

            uiState.successMessage?.let { msg ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = msg, color = Color(0xFF2E7D32))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- Botón Iniciar Sesión  HAY QUE TERMINAR EL COMPONENTE---
            Buttons(
                text = "Confirmar",
                onClick = {
                    if (!isFirstLoginMode && token.isBlank()) return@Buttons
                    if (isFirstLoginMode && (correo.isBlank() || passwordActual.isBlank())) return@Buttons
                    if (newPassword.isBlank() || confirmPassword.isBlank()) return@Buttons
                    if (newPassword != confirmPassword) return@Buttons
                    if (isFirstLoginMode) {
                        viewModel.changeOnFirstLogin(
                            correo = correo,
                            passwordActual = passwordActual,
                            newPassword = newPassword,
                            onSuccess = onPasswordUpdated
                        )
                    } else {
                        viewModel.resetWithToken(
                            token = token,
                            newPassword = newPassword,
                            onSuccess = onPasswordUpdated
                        )
                    }
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun pre4(){
    ScreeanCreatePassword()
}