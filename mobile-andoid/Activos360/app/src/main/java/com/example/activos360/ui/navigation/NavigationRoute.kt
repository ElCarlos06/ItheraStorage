package com.example.activos360.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.activos360.ui.screens.Login.LoginScreen
import com.example.activos360.ui.screens.Login.ScreeanCreatePassword
import com.example.activos360.ui.screens.Login.ScreenPassword
import com.example.activos360.ui.screens.Empleado.EmpleadoMainScreen
import com.example.activos360.ui.screens.Empleado.details.ConfirmarResguardoScreen
import com.example.activos360.ui.screens.Empleado.details.DetallesActivoScreen
import com.example.activos360.ui.screens.Empleado.details.DevolverActivoScreen
import com.example.activos360.ui.screens.Empleado.details.ReportarDanoScreen
import androidx.compose.runtime.remember
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.ui.screens.tecnico.ReportesTecnicoScreen
import com.example.activos360.ui.screens.tecnico.TecnicoMainScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    val startDestination = remember {
        if (TokenManager.isTokenValid()) {
            val role = TokenManager.getRoleFromToken() ?: ""
            when {
                role.contains("Empleado", ignoreCase = true) -> "home_empleado"
                role.contains("Tecnico", ignoreCase = true) -> "home_tecnico"
                else -> "home_admin"
            }
        } else {
            "login"
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // LOGIN
        composable("login") {
            LoginScreen(
                onLoginSuccess = { rutaDestino ->
                    // Como el ViewModel ya decidió a qué ruta ir, navegamos directo
                    navController.navigate(rutaDestino) {
                        // ESTO ES PARA QUE NO VUELVA A LA PANTALLA DE LOGIN
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                }
            )
        }

        // RUTA DEL EMPLEADO
        composable("home_empleado") {
            EmpleadoMainScreen(navController)
        }

        // RUTA DEL TÉCNICO
        composable("home_tecnico") {
            TecnicoMainScreen(navController)
        }

        // Alias para no romper navegación vieja (LoginViewModel aún usa home_admin)
        composable("home_admin") {
            //EmpleadoMainScreen(navController)
        }

        composable(
            route = "detalles_activo/{activoId}",
            arguments = listOf(navArgument("activoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val activoIdStr = backStackEntry.arguments?.getString("activoId").orEmpty()
            val activoId = activoIdStr.toLongOrNull() ?: 0L
            DetallesActivoScreen(
                activoId = activoId,
                onBack = { navController.popBackStack() },
                onResguardarClick = { navController.navigate("confirmar_resguardo/$activoId") },
                onReportarDanoClick = { id, etiqueta, nombre ->
                    navController.navigate(
                        "reportar_dano/$id/${Uri.encode(etiqueta)}/${Uri.encode(nombre)}"
                    )
                },
                onDevolverActivoClick = { id, etiqueta, nombre ->
                    navController.navigate(
                        "devolver_activo/$id/${Uri.encode(etiqueta)}/${Uri.encode(nombre)}"
                    )
                }
            )
        }

        composable(
            route = "confirmar_resguardo/{activoId}",
            arguments = listOf(navArgument("activoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val activoId = backStackEntry.arguments?.getLong("activoId") ?: 0L
            ConfirmarResguardoScreen(
                activoId = activoId,
                onBack = { navController.popBackStack() },
                onConfirmed = {
                    // regresamos a detalles y refrescamos
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "reportar_dano/{activoId}/{activoEtiqueta}/{activoNombre}",
            arguments = listOf(
                navArgument("activoId") { type = NavType.LongType },
                navArgument("activoEtiqueta") { type = NavType.StringType },
                navArgument("activoNombre") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val activoId = backStackEntry.arguments?.getLong("activoId") ?: 0L
            val etiqueta = backStackEntry.arguments?.getString("activoEtiqueta").orEmpty()
            val nombre = backStackEntry.arguments?.getString("activoNombre").orEmpty()
            ReportarDanoScreen(
                activoId = activoId,
                activoEtiqueta = etiqueta,
                activoNombre = nombre,
                onBack = { navController.popBackStack() },
                onReportarSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = "devolver_activo/{activoId}/{activoEtiqueta}/{activoNombre}",
            arguments = listOf(
                navArgument("activoId") { type = NavType.LongType },
                navArgument("activoEtiqueta") { type = NavType.StringType },
                navArgument("activoNombre") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val activoId = backStackEntry.arguments?.getLong("activoId") ?: 0L
            val etiqueta = backStackEntry.arguments?.getString("activoEtiqueta").orEmpty()
            val nombre = backStackEntry.arguments?.getString("activoNombre").orEmpty()
            DevolverActivoScreen(
                activoId = activoId,
                activoEtiqueta = etiqueta,
                activoNombre = nombre,
                onBack = { navController.popBackStack() },
                onDevolverSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = "reporte_tecnico/{activoId}/{mantenimientoId}",
            arguments = listOf(
                navArgument("activoId") { type = NavType.LongType },
                navArgument("mantenimientoId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val activoId = backStackEntry.arguments?.getLong("activoId") ?: 0L
            val mantenimientoId = backStackEntry.arguments?.getLong("mantenimientoId") ?: 0L
            ReportesTecnicoScreen(
                activoId = activoId,
                mantenimientoId = mantenimientoId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("forgot_password") {
            ScreenPassword(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla para restablecer con token (similar al front web)
        composable(
            route = "create_password?token={token}&correo={correo}",
            arguments = listOf(navArgument("token") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            }, navArgument("correo") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            val correo = backStackEntry.arguments?.getString("correo")
            ScreeanCreatePassword(
                tokenFromLink = token?.takeIf { it.isNotBlank() },
                correoFromFirstLogin = correo?.takeIf { it.isNotBlank() },
                onBackClick = { navController.popBackStack() },
                onPasswordUpdated = {
                    // al completar, regresamos a login
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
    }
}
