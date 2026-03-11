package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class EmpleadoViewModel : ViewModel() {

    var nombreUsuario by  mutableStateOf("Mena")
        private  set

    var fotoUsuario by mutableStateOf<String?>(null)
        private set
}

