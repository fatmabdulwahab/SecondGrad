package com.example.secondgrad.screens.scoffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.secondgrad.screens.FilterSection
import com.example.secondgrad.screens.MetroScreen
import com.example.secondgrad.screens.busCard.BusScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldScreen(navController: NavController) {

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // الفلتر المختار
    var selectedFilter by remember {
        mutableStateOf("الكل")
    }

    // بيانات مؤقتة للـ UI
    val routes = listOf(
        "باص",
        "مترو",
        "باص"
    )

    // الفلترة
    val filteredRoutes = when (selectedFilter) {

        "الكل" -> routes

        else -> routes.filter {
            it == selectedFilter
        }
    }

    ModalNavigationDrawer(

        drawerState = drawerState,

        drawerContent = {

            ModalDrawerSheet {

                Text(
                    text = "Menu",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

    ) {

        Scaffold(

            topBar = {

                CenterAlignedTopAppBar(

                    title = {

                        Text(
                            text = "Home",
                            fontWeight = FontWeight.Bold
                        )
                    },

                    navigationIcon = {

                        IconButton(

                            onClick = {

                                scope.launch {
                                    drawerState.open()
                                }
                            }

                        ) {

                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null
                            )
                        }
                    }
                )
            },

            bottomBar = {
                AppBottomBar()
            }

        ) { paddingValues ->

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF59B484))
                    .padding(paddingValues),

                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "YOUR JOURNEY STARTS HERE",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Search
                SearchSection(
                    context = context,
                    lifecycleOwner = lifecycleOwner
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Filter
                FilterSection(

                    selected = selectedFilter,

                    onSelectedChange = {
                        selectedFilter = it
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(

                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .weight(1f),

                    verticalArrangement = Arrangement.spacedBy(12.dp),

                    contentPadding = PaddingValues(bottom = 16.dp)

                ) {

                    items(filteredRoutes) { route ->

                        if (route == "باص") {

                            BusScreen()

                        } else {

                            MetroScreen()
                        }
                    }
                }
            }
        }
    }
}