package com.example.secondgrad.navigationgraph


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.secondgrad.screens.login.ForgotPasswordScreen
import com.example.secondgrad.screens.login.LoginScreen
import com.example.secondgrad.screens.login.RegisterScreen
import com.example.secondgrad.screens.login.SplashScreen
import com.example.secondgrad.screens.scoffold.BusRoutesScreen
import com.example.secondgrad.screens.scoffold.CommonQuestionScreen
import com.example.secondgrad.screens.scoffold.ContactUs
import com.example.secondgrad.screens.scoffold.DashboardScreen

import com.example.secondgrad.screens.scoffold.HistoryScreen
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

        composable("forgot_password") {
            ForgotPasswordScreen(navController)
        }

        composable("home") {
            ScaffoldScreen(navController)
        }

        composable("dashboard") {
            DashboardScreen(navController = navController)
        }

        composable("routes_screen") {

            BusRoutesScreen()
        }

        composable("history_screen") {

            HistoryScreen()
        }

        composable("commonquestionscreen") {

            CommonQuestionScreen()
        }
       // the route
        composable("contactus") {

            ContactUs()
        }
    }

    }