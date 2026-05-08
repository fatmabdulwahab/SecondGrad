package com.example.secondgrad.screens.login


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.secondgrad.R
import kotlinx.coroutines.delay



@Composable
fun SplashScreen(navController : NavController){
/*
    LaunchedEffect(Unit) {
        delay(3000)
        onTimeout()
    }

 */
    LaunchedEffect(Unit) {
        delay(3000)

        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.background_green)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ){

        Image(
            painter = painterResource(id = R.drawable.bus_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.padding(20.dp))

        Text(
            text = "Tareeqy",
            fontWeight =FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White

        )

        Spacer(modifier = Modifier.padding(20.dp))

        Text(
            text = "طريقك اسهل معانا",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White

        )
    }
}






