package com.example.secondgrad.screens.scoffold

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.secondgrad.DashboardStats
import com.example.secondgrad.DashboardViewModel
import com.example.secondgrad.RoleResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val roles by viewModel.roles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showAddRoleDialog by remember { mutableStateOf(false) }
    var editingRole by remember { mutableStateOf<RoleResponse?>(null) }

    LaunchedEffect(message) {
        val currentMessage = message
        if (currentMessage != null) {
            Toast.makeText(context, currentMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    if (showAddRoleDialog) {
        RoleNameDialog(
            title = "إضافة صلاحية جديدة",
            initialValue = "",
            confirmLabel = "إضافة",
            onDismiss = { showAddRoleDialog = false },
            onConfirm = { roleName ->
                viewModel.addRole(roleName)
                showAddRoleDialog = false
            }
        )
    }

    val roleToEdit = editingRole
    if (roleToEdit != null) {
        RoleNameDialog(
            title = "تعديل الصلاحية",
            initialValue = roleDisplayName(roleToEdit),
            confirmLabel = "حفظ",
            onDismiss = { editingRole = null },
            onConfirm = { roleName ->
                viewModel.editRole(roleToEdit, roleName)
                editingRole = null
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                scope = scope,
                drawerState = drawerState,
                navController = navController
            )
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Dashboard",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2F965D))
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            RolesSection(
                                roles = roles,
                                isLoading = isLoading,
                                onAddRole = { showAddRoleDialog = true },
                                onEditRole = { role -> editingRole = role },
                                onDeleteRole = viewModel::deleteRole
                            )

                            StatsSection(
                                stats = stats,
                                isLoading = isLoading
                            )
                        }
                    }

                    item {
                        StatsChartCard(
                            stats = stats,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleNameDialog(
    title: String,
    initialValue: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var roleName by remember(initialValue) { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = roleName,
                onValueChange = { roleName = it },
                label = { Text("اسم الصلاحية") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(roleName) }) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

private fun roleDisplayName(role: RoleResponse): String {
    val name = role.name
    return if (name == null) "" else name
}

@Composable
private fun SectionTitle(text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .width(70.dp)
                .height(2.dp)
                .background(Color(0xFFFFB347))
        )
    }
}

@Composable
private fun StatsSection(
    stats: DashboardStats,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionTitle("إحصائيات")
        Spacer(modifier = Modifier.height(28.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(235.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isLoading) {
                    Text(
                        text = "...جاري التحديث",
                        color = Color(0xFF2F965D),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    CircularProgressIndicator(color = Color(0xFF2F965D))
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatCircle(value = stats.feedbacksCount, label = "التقييمات")
                        StatCircle(value = stats.tripsCount, label = "الرحلات")
                        StatCircle(value = stats.usersCount, label = "المستخدمين")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCircle(
    value: Int,
    label: String
) {
    Box(
        modifier = Modifier
            .size(116.dp)
            .border(4.dp, Color(0xFF2F965D), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value.toString(),
                color = Color(0xFF2F965D),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color(0xFF2F965D),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RolesSection(
    roles: List<RoleResponse>,
    isLoading: Boolean,
    onAddRole: () -> Unit,
    onEditRole: (RoleResponse) -> Unit,
    onDeleteRole: (RoleResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionTitle("قائمة الصلاحيات")
        Spacer(modifier = Modifier.height(28.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                RolesTableHeader()

                if (isLoading && roles.size == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2F965D))
                    }
                } else if (roles.size == 0) {
                    Text(
                        text = "لا توجد صلاحيات متاحة",
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    for (index in roles.indices) {
                        RoleRow(
                            index = index + 1,
                            role = roles[index],
                            onEditRole = onEditRole,
                            onDeleteRole = onDeleteRole
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddRole,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9233)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "إضافة صلاحية جديدة",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RolesTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color(0xFFFF9233))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.4f))
        Text("الاسم", color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1.4f))
        Text("التحكم", color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1.2f))
    }
}

@Composable
private fun RoleRow(
    index: Int,
    role: RoleResponse,
    onEditRole: (RoleResponse) -> Unit,
    onDeleteRole: (RoleResponse) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = index.toString(),
            color = Color(0xFF9CA3AF),
            modifier = Modifier.weight(0.4f)
        )

        Text(
            text = roleDisplayName(role),
            color = Color(0xFF2F965D),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.4f)
        )

        Row(
            modifier = Modifier.weight(1.2f),
            horizontalArrangement = Arrangement.Center
        ) {
            SmallActionButton(
                text = "تعديل",
                color = Color(0xFF2F965D),
                onClick = { onEditRole(role) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            SmallActionButton(
                text = "حذف",
                color = Color(0xFFFF9233),
                onClick = { onDeleteRole(role) }
            )
        }
    }

    HorizontalDivider(color = Color(0xFFE5E7EB))
}

@Composable
private fun SmallActionButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        modifier = Modifier.height(28.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
private fun StatsChartCard(
    stats: DashboardStats,
    modifier: Modifier = Modifier
) {
    val chartItems = listOf(
        "التقييمات" to stats.feedbacksCount,
        "الرحلات" to stats.tripsCount,
        "المستخدمين" to stats.usersCount
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier.height(330.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
        ) {
            DashboardLineChart(
                items = chartItems,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun DashboardLineChart(
    items: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val green = Color(0xFF2F965D)
    val orange = Color(0xFFFF9233)
    val axis = Color(0xFFD1D5DB)

    var maxValue = 1
    for (item in items) {
        if (item.second > maxValue) {
            maxValue = item.second
        }
    }

    val pointCount = if (items.size > 1) items.size - 1 else 1

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val left = 54.dp.toPx()
            val bottom = size.height - 44.dp.toPx()
            val right = size.width - 26.dp.toPx()
            val top = 34.dp.toPx()
            val width = right - left
            val height = bottom - top

            drawLine(
                color = axis,
                start = Offset(left, top),
                end = Offset(left, bottom),
                strokeWidth = 3.dp.toPx()
            )
            drawLine(
                color = axis,
                start = Offset(left, bottom),
                end = Offset(right, bottom),
                strokeWidth = 3.dp.toPx()
            )

            val points = java.util.ArrayList<Offset>()
            for (index in items.indices) {
                val item = items[index]
                val x = left + (width / pointCount) * index
                val y = bottom - ((item.second / maxValue.toFloat()) * height)
                points.add(Offset(x, y))
            }

            val path = Path()
            for (index in points.indices) {
                val point = points[index]
                if (index == 0) {
                    path.moveTo(point.x, point.y)
                } else {
                    path.lineTo(point.x, point.y)
                }
            }

            drawPath(
                path = path,
                color = orange,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )

            for (point in points) {
                drawCircle(color = green, radius = 9.dp.toPx(), center = point)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 32.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (item in items) {
                Text(
                    text = item.first,
                    color = green,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 32.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (item in items) {
                Text(
                    text = item.second.toString(),
                    color = green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
