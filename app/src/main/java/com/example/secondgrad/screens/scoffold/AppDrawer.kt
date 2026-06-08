package com.example.secondgrad.screens.scoffold

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Segment
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.secondgrad.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController

@Composable
fun AppDrawer(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavHostController)
{
    val primaryGreen = Color(0xFF59B484)

    ModalDrawerSheet {

        // عنوان القائمة
        Text(
            text = "Menu",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(24.dp)
        )

        // العناصر
        NavigationDrawerItem(
            label = { Text("Dashboard",
                    color = primaryGreen)
                    },
            selected = false,
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.dash_board),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate("dashboard")
            }
        )

        NavigationDrawerItem(
            label = { Text("History",color = primaryGreen) },
            selected = false,
            icon = {

                    Image(
                        painter = painterResource(id = R.drawable.history),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

            },
            onClick = { scope.launch { drawerState.close() }
                navController.navigate("history_screen")
            }
        )

        NavigationDrawerItem(
            label = { Text("Our Routes",color = primaryGreen) },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Route,
                    contentDescription = null,
                    tint = primaryGreen
                )
            },
            onClick = { scope.launch { drawerState.close() }
                navController.navigate("routes_screen")}
        )

        NavigationDrawerItem(
            label = { Text("Contact us",color = primaryGreen) },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = primaryGreen
                )
            },
            onClick = { scope.launch { drawerState.close() }
            navController.navigate("contactus")}
        )

        NavigationDrawerItem(
            label = { Text("Common question",color = primaryGreen) },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = primaryGreen
                )
            },
            onClick = { scope.launch { drawerState.close() }
                navController.navigate("commonquestionscreen")
            }
        )
    }
}