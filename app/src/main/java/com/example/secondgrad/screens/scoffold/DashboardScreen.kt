package com.example.secondgrad.screens.scoffold

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.secondgrad.DashboardStats
import com.example.secondgrad.DashboardViewModel
import com.example.secondgrad.RoleResponse

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val roles by viewModel.roles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2F965D))
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    RolesSection(
                        roles = roles,
                        isLoading = isLoading,
                        onDeleteRole = viewModel::deleteRole,
                        modifier = Modifier.weight(1f)
                    )

                    StatsSection(
                        stats = stats,
                        isLoading = isLoading,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    StatsChartCard(
                        stats = stats,
                        modifier = Modifier.fillMaxWidth(0.52f)
                    )
                }
            }
        }
    }
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
                    Text(
                        text = "...جاري التحديث",
                        color = Color(0xFF2F965D),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(28.dp))

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

                if (isLoading && roles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2F965D))
                    }
                } else if (roles.isEmpty()) {
                    Text(
                        text = "لا توجد صلاحيات متاحة",
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    roles.forEachIndexed { index, role ->
                        RoleRow(
                            index = index + 1,
                            role = role,
                            onDeleteRole = onDeleteRole
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9233)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(48.dp)
        ) {
            androidx.compose.material3.Icon(
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
            text = role.name.orEmpty(),
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
                onClick = { }
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
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
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
        modifier = modifier
            .height(330.dp)
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
    val maxValue = items.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1

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

            val points = items.mapIndexed { index, item ->
                val x = left + (width / (items.lastIndex.coerceAtLeast(1))) * index
                val y = bottom - ((item.second / maxValue.toFloat()) * height)
                Offset(x, y)
            }

            val path = Path().apply {
                points.forEachIndexed { index, point ->
                    if (index == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
                }
            }

            drawPath(
                path = path,
                color = orange,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )

            points.forEach {
                drawCircle(color = green, radius = 9.dp.toPx(), center = it)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 32.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
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
            items.forEach { item ->
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
