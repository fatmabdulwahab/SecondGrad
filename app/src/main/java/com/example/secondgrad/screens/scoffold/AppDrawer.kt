package com.example.secondgrad.screens.scoffold

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Segment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(scope: CoroutineScope, drawerState: DrawerState) {
    ModalDrawerSheet {
        Text(
            text = "Menu",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        )

        NavigationDrawerItem(
            label = { Text("Dashboard") },
            selected = false,
            icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
            onClick = { scope.launch { drawerState.close() } }
        )

        NavigationDrawerItem(
            label = { Text("History") },
            selected = false,
            icon = { Icon(Icons.AutoMirrored.Filled.Segment, contentDescription = null) },
            onClick = { scope.launch { drawerState.close() } }
        )

        NavigationDrawerItem(
            label = { Text("Our Routes") },
            selected = false,
            onClick = { scope.launch { drawerState.close() } }
        )

        NavigationDrawerItem(
            label = { Text("Contact us") },
            selected = false,
            onClick = { scope.launch { drawerState.close() } }
        )
    }
}