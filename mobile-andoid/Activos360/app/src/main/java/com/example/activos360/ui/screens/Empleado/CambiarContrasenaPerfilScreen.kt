package com.example.activos360.ui.screens.Empleado

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
import com.example.activos360.R
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.components.MoonIcon
import com.example.activos360.ui.components.MoonIcons
import com.example.activos360.ui.viewmodel.ChangePasswordViewModel

@Composable
fun CambiarContrasenaPerfilScreen(
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {},
    viewModel: ChangePasswordViewModel = viewModel()
) {
    var passwordActual    by remember { mutableStateOf("") }
    var newPassword       by remember { mutableStateOf("") }
    var confirmPassword   by remember { mutableStateOf("") }
    var passwordVisible   by remember { mutableStateOf(false) }
    var localError        by remember { mutableStateOf<String?>(null) }

    val primaryColor   = Color(0xFF7B88FF)
    val secondaryColor = Color(0xFF000000)

    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) onSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {
            IconButton(onClick = onBack) {
                MoonIcon(icon = MoonIcons.ArrowsLeft, contentDescription = "Regresar")
            }

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

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Cambiar contraseña",
                color = secondaryColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // Contraseña actual
            Text(
                text = "Contraseña actual",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = passwordActual,
                onValueChange = { passwordActual = it; localError = null },
                placeholder = { Text("Escribe tu contraseña actual", color = Color.Gray) },
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
                enabled = !viewModel.isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Nueva contraseña
            Text(
                text = "Nueva Contraseña",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it; localError = null },
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
                enabled = !viewModel.isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Confirmar contraseña
            Text(
                text = "Confirmar Contraseña",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; localError = null },
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
                enabled = !viewModel.isLoading
            )

            // Errores locales o del ViewModel
            val displayError = localError ?: viewModel.errorMessage
            if (displayError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = displayError, color = Color(0xFFD33030))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Buttons(
                text = "Confirmar",
                onClick = {
                    when {
                        passwordActual.isBlank()          -> localError = "Ingresa tu contraseña actual"
                        newPassword.isBlank()             -> localError = "Ingresa la nueva contraseña"
                        newPassword.length < 6            -> localError = "La contraseña debe tener al menos 6 caracteres"
                        newPassword != confirmPassword    -> localError = "Las contraseñas no coinciden"
                        else -> viewModel.changePassword(passwordActual, newPassword)
                    }
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CambiarContrasenaPerfilPreview() {
    CambiarContrasenaPerfilScreen()
}
