package com.example.activos360.ui.Screens.Empleado

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activos360.R
import com.example.activos360.ui.components.Canvas2
import com.example.activos360.ui.components.WaveHeader
import com.example.activos360.ui.viewmodel.EmpleadoViewModel
@Composable
fun HomeEmpleado(viewModel: EmpleadoViewModel = viewModel()) {

    val primaryColor = Color(0xFF7B88FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Canvas de el usuario XDDDD
        Canvas2()
        Spacer(modifier = Modifier.height(32.dp))


        Box {
            Row(

            ) {
                
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun home(){
    HomeEmpleado()
}