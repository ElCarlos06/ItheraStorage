package com.example.activos360.ui.Screens.Login

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.activos360.R
import com.example.activos360.ui.components.Buttons
@Composable
fun ScreenPassword(
    onBackClick: () -> Unit = {}
) {
    // Estados para los textos
    var email by remember { mutableStateOf("") }

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
            IconButton(
                onClick = {
                    onBackClick()
                },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                )
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
                text = "¡Oops! Olvidaste tu contraseña?",
                color = secondaryColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(70.dp))

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

            Spacer(modifier = Modifier.height(265.dp))

            // --- Botón Iniciar Sesión  HAY QUE TERMINAR EL COMPONENTE---
            Buttons(
                text = "Confirmar",
                //Logia=ca de mandar el correo
                onClick = {}
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun pre3(){
    ScreenPassword()
}