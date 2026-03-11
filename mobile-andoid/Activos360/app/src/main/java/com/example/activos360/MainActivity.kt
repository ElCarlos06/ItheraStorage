package com.example.activos360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.activos360.ui.ScreensLogin.LoginScreen
import com.example.activos360.ui.ScreensLogin.ScreeanCreatePassword
import com.example.activos360.ui.screens_login.LoginInitialScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScreeanCreatePassword()
        }
    }
}

