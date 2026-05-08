package com.example.secondgrad.screens.scoffold

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppBottomBar() {
    NavigationBar(
        modifier = Modifier.height(60.dp),
        containerColor = Color.White,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = {},
            alwaysShowLabel = false,
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            alwaysShowLabel = false,
            icon = { Icon(Icons.Filled.Inbox, contentDescription = "Inbox", modifier = Modifier.size(26.dp)) }
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            alwaysShowLabel = false,
            icon = { Icon(Icons.Filled.Person, contentDescription = "Person") }
        )
    }
}