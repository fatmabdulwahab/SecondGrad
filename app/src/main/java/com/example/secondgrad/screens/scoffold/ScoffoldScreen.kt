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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.secondgrad.screens.FilterSection
import com.example.secondgrad.screens.busCard.BusScreen
import kotlinx.coroutines.launch
import com.example.secondgrad.RouteViewModel
import androidx.compose.runtime.collectAsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldScreen(navController: NavController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val routeViewModel: RouteViewModel = viewModel()
    val routes by routeViewModel.routes.collectAsState()
    val fromText by routeViewModel.fromText.collectAsState()
    val toText by routeViewModel.toText.collectAsState()
    val isLoading by routeViewModel.isLoading.collectAsState()

   var selectedFilter by remember { mutableStateOf("الكل") }
    val filteredRoutes = remember(routes, selectedFilter) {
        when (selectedFilter) {
            "باص" -> routes.filter { route ->
                route.routeDetails.any { it.routeName.startsWith("M", ignoreCase = true) }
            }
            "مترو" -> routes.filter { route ->
                route.routeDetails.any { hasTextPart(it.routeName, "الخط") }
            }
            else -> routes
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // استدعاء الدالة الموحدة للقائمة الجانبية
            AppDrawer(scope = scope, drawerState = drawerState,
                navController = navController as NavHostController)
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Home", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                        }
                    }
                )
            },
            bottomBar = { AppBottomBar() }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF59B484))
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "YOUR JOURNEY STARTS HERE", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))
                SearchSection(viewModel = routeViewModel)
                Spacer(modifier = Modifier.height(20.dp))
                FilterSection(selected = selectedFilter, onSelectedChange = { selectedFilter = it })
                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading && routes.size == 0) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(0.96f)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {

                        items(filteredRoutes) { route ->

                            BusScreen(
                                route = route,
                                userLocation = fromText,
                                destination = toText
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun hasTextPart(value: String, part: String): Boolean {
    if (part.length == 0 || value.length < part.length) {
        return false
    }

    var start = 0
    while (start <= value.length - part.length) {
        var offset = 0
        var matched = true

        while (offset < part.length) {
            if (value[start + offset] != part[offset]) {
                matched = false
                break
            }
            offset++
        }

        if (matched) {
            return true
        }
        start++
    }

    return false
}