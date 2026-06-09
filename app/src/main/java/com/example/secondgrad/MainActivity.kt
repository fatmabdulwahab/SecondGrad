package com.example.secondgrad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.secondgrad.AuthSession
import com.example.secondgrad.navigationgraph.NavigationGraph
import com.example.secondgrad.screens.login.SplashScreen
import com.example.secondgrad.screens.scoffold.ScaffoldScreen
import com.example.secondgrad.ui.theme.SecondGradTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthSession.init(applicationContext)
        enableEdgeToEdge()

        setContent {
            SecondGradTheme {

                val navController = rememberNavController()

                NavigationGraph(navController = navController)
//                CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
//
//
//                    NavigationGraph(navController = navController)
//                }
            }
        }
    }
}
