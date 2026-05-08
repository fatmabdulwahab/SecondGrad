package com.example.secondgrad.navigationgraph


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.secondgrad.screens.login.LoginScreen
import com.example.secondgrad.screens.login.RegisterScreen
import com.example.secondgrad.screens.login.SplashScreen
import com.example.secondgrad.screens.scoffold.ScaffoldScreen

@Composable
fun NavigationGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)

        }

        composable("home") {
            ScaffoldScreen(navController)
        }


    }

    }