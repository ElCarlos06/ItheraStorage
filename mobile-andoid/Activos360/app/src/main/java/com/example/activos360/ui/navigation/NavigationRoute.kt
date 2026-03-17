package com.example.activos360.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.activos360.ui.screens.Login.LoginScreen
import com.example.activos360.ui.screens.Login.ScreenPassword
import com.example.activos360.ui.screens.Empleado.EmpleadoMainScreen
import com.example.activos360.ui.screens.tecnico.TecnicoMainScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // LOGIN
        composable("login") {
            LoginScreen(
                onLoginSuccess = { rutaDestino ->
                    // Como el ViewModel ya decidió a qué ruta ir, navegamos directo
                    navController.navigate(rutaDestino) {
                        // ESTO ES PARA QUE NO VUELVA A LA PANTALLA DE LOGIN
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // RUTA DEL EMPLEADO
        composable("home_empleado") {
            EmpleadoMainScreen(navController)
        }

        // RUTA DEL TÉCNICO
        composable("home_admin") {
            TecnicoMainScreen(navController)
        }

        composable("forgot_password") {
            ScreenPassword(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
