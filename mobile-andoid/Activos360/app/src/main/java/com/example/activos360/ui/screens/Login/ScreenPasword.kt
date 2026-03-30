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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import com.example.activos360.ui.components.MoonIcon
import com.example.activos360.ui.components.MoonIcons
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.R
import com.example.activos360.ui.components.Buttons
import com.example.activos360.ui.viewmodel.ForgotPasswordViewModel

@Composable
fun ScreenPassword(
    onBackClick: () -> Unit = {},
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    val primaryColor = Color(0xFF7B88FF)
    val secondaryColor = Color(0xFF000000)

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
            IconButton(onClick = { onBackClick() }) {
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
                text = "¡Oops! Olvidaste tu contraseña?",
                color = secondaryColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(70.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
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
                singleLine = true,
                enabled = !uiState.isLoading && uiState.successMessage == null
            )

            uiState.errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = msg, color = Color(0xFFD33030), fontSize = 14.sp)
            }

            uiState.successMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = msg, color = Color(0xFF2E7D32), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.successMessage == null) {
                Buttons(
                    text = if (uiState.isLoading) "Enviando..." else "Confirmar",
                    onClick = {
                        if (email.isNotBlank()) viewModel.requestReset(email)
                    },
                    enabled = email.isNotBlank() && !uiState.isLoading
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun pre3() {
    ScreenPassword()
}
