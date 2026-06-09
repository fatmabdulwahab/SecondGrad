package com.example.secondgrad.screens.scoffold

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.secondgrad.R

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    navController: NavHostController
) {
    val primaryGreen = Color(0xFF59B484)
    var navigationTarget by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(navigationTarget) {
        val target = navigationTarget
        if (target != null) {
            drawerState.close()
            navController.navigate(target)
            navigationTarget = null
        }
    }

    fun navigateTo(route: String) {
        navigationTarget = route
    }

    ModalDrawerSheet {
        Text(
            text = "Menu",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(24.dp)
        )

        NavigationDrawerItem(
            label = {
                Text(
                    "Dashboard",
                    color = primaryGreen
                )
            },
            selected = false,
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.dash_board),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            onClick = { navigateTo("dashboard") }
        )

        NavigationDrawerItem(
            label = { Text("History", color = primaryGreen) },
            selected = false,
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.history),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            onClick = { navigateTo("history_screen") }
        )

        NavigationDrawerItem(
            label = { Text("Our Routes", color = primaryGreen) },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Route,
                    contentDescription = null,
                    tint = primaryGreen
                )
            },
            onClick = { navigateTo("routes_screen") }
        )

        NavigationDrawerItem(
            label = { Text("Contact us", color = primaryGreen) },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = primaryGreen
                )
            },
            onClick = { navigateTo("contactus") }
        )

        NavigationDrawerItem(
            label = { Text("Common question", color = primaryGreen) },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = primaryGreen
                )
            },
            onClick = { navigateTo("commonquestionscreen") }
        )
    }
}
