package com.example.secondgrad.screens.busCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.secondgrad.RouteData
import com.example.secondgrad.RouteDetail

@Composable
fun BusScreen(
    route: RouteData,
    userLocation: String,
    destination: String,
    modifier: Modifier = Modifier
) {
    val segments = route.routeDetails
    val totalCost = segments.sumOf { it.ticketPrice }
    val totalTime = segments.sumOf { it.averageTimeInMinutes }
    val startStation = route.closestStationName.ifBlank { userLocation }
    val endStation = destination.ifBlank { segments.lastOrNull()?.stations?.lastOrNull().orEmpty() }

    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                RouteHeader(route = route, segments = segments)

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(12.dp))

                TimeAndCost(
                    totalCost = totalCost,
                    totalTimeInMinutes = totalTime
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    TripTimeline(
                        stepsCount = segments.size,
                        transferCount = route.transferStations.size
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        LocationLabel(
                            title = "انت هنا",
                            value = startStation,
                            titleColor = Color(0xFFFF7A00)
                        )

                        segments.forEachIndexed { index, segment ->
                            SegmentCard(segment = segment)

                            if (index < segments.lastIndex) {
                                TransferCard(
                                    stationName = route.transferStations.getOrNull(index)
                                        ?: "محطة التحويل"
                                )
                            }
                        }

                        LocationLabel(
                            title = "هتنزل",
                            value = endStation,
                            titleColor = Color(0xFF22C55E)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF22C55E),
                                Color(0xFFFF7A00)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun RouteHeader(
    route: RouteData,
    segments: List<RouteDetail>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TransferBadge(routeType = route.routeType)

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "خطة الرحلة",
                color = Color(0xFF9CA3AF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                segments.forEachIndexed { index, segment ->
                    ChipItem(
                        text = segment.routeName,
                        isSelected = index == segments.lastIndex
                    )

                    if (index < segments.lastIndex) {
                        Text(
                            text = "<",
                            color = Color(0xFFFF7A00),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransferBadge(routeType: String) {
    val label = when {
        routeType.equals("Direct", ignoreCase = true) -> "مباشر"
        routeType.startsWith("1 ") -> "Transfer 1"
        routeType.startsWith("2 ") -> "Transfer 2"
        else -> routeType
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1F22C55E))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF047857),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LocationLabel(
    title: String,
    value: String,
    titleColor: Color
) {
    Text(
        text = title,
        color = titleColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp)
    )

    Text(
        text = value,
        color = Color(0xFF111827),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.End,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
    )
}

@Composable
private fun SegmentCard(segment: RouteDetail) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Color(0xFFE5E7EB)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(5.dp)
                    .background(Color(0xFFFF7A00))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFF4E6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = Color(0xFFFF7A00),
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = segment.routeName,
                    color = Color(0xFFFF7A00),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = " :هتركب",
                    color = Color(0xFF6B7280),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun TransferCard(stationName: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF4)),
        border = BorderStroke(0.5.dp, Color(0x1F22C55E)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = ":هتحول في",
                    color = Color(0xFF047857),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stationName,
                    color = Color(0xFF111827),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun TripTimeline(
    stepsCount: Int,
    transferCount: Int
) {
    val lineHeight = (120 + stepsCount * 58 + transferCount * 46).dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 2.dp)
    ) {
        Circle()

        Box(
            modifier = Modifier
                .width(2.dp)
                .height(lineHeight)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF7A00),
                            Color(0xFF22C55E)
                        )
                    )
                )
        )

        TelegramIcon()
    }
}
