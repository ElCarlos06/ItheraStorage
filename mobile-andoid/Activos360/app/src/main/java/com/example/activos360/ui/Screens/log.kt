package com.example.activos360.ui.Screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activos360.ui.components.Button

@Composable
fun LoginScreen() {
    // Estados para los textos
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(24.dp))

            // --- Botón Iniciar Sesión  HAY QUE TERMINAR EL COMPONENTE---
            Button()
        }
    }
}

// --- COMPONENTE DE LA OLA  HAY Q REUTILIZARLO PARA LAS DEMAS VISTAS---
@Composable
fun WaveHeader(color: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp) // Altura de la cabecera azul
    ) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, 0f) // Empezamos en la esquina superior izquierda
            lineTo(0f, height * 0.8f) // Bajamos por el borde izquierdo


            // Aquí creamos la curva de la ola
            cubicTo(
                x1 = width * 0.3f, y1 = height * 0.6f,  // Punto de control 1 (tira la curva hacia arriba)
                x2 = width * 0.7f, y2 = height * 1.1f,  // Punto de control 2 (tira la curva hacia abajo)
                x3 = width, y3 = height * 0.8f          // Punto final en el borde derecho
            )

            lineTo(width, 0f) // Sube al borde superior derecho
            close()
        }

        drawPath(path = path, color = color)
    }
}


@Composable
@Preview(showBackground = true)
fun pre2(){
    LoginScreen()
}