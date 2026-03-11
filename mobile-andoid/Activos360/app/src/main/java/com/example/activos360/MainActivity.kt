package com.example.activos360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.activos360.ui.Screens.Login.LoginScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.activos360.ui.screens.tests.HomeScreen
import com.example.activos360.ui.screens.tests.LoginTest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Inicializamos el controlador de navegación
            val navController = rememberNavController()

            // Definimos las rutas de nuestra app
            NavHost(navController = navController, startDestination = "login") {

                // Ruta 1: El Login
                composable("login") {
                    LoginTest(
                        onNavigateToHome = {
                            // Cuando el login es exitoso, navegamos al Home
                            // y borramos el Login del historial para que el usuario
                            // no vuelva atrás si presiona el botón de retroceso
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                // Ruta 2: La vista vacía (Home)
                composable("home") {
                    HomeScreen()
                }
            }
        }
    }
}

