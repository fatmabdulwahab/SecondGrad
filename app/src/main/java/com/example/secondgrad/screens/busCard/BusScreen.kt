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
import androidx.compose.material.icons.filled.Train
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
    val totalCost = calculateTotalCost(segments)
    val totalTime = calculateTotalTime(segments)
    val startStation = if (route.closestStationName.length == 0) {
        userLocation
    } else {
        route.closestStationName
    }
    val endStation = getEndStation(destination, segments)

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

                        var index = 0
                        while (index < segments.size) {
                            val segment = segments[index]
                            SegmentCard(segment = segment)

                            if (index != segments.size - 1) {
                                val stationName = if (index < route.transferStations.size) {
                                    route.transferStations[index]
                                } else {
                                    "محطة التحويل"
                                }

                                TransferCard(stationName = stationName)
                            }

                            index++
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
                            colors = gradientColors()
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
                var index = 0
                while (index < segments.size) {
                    val segment = segments[index]
                    ChipItem(
                        text = segment.routeName,
                        isSelected = index == segments.size - 1
                    )

                    if (index != segments.size - 1) {
                        Text(
                            text = "<",
                            color = Color(0xFFFF7A00),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    index++
                }
            }
        }
    }
}

@Composable
private fun TransferBadge(routeType: String) {
    val label = when {
        routeType == "Direct" -> "مباشر"
        startsWithText(routeType, "1 ") -> "Transfer 1"
        startsWithText(routeType, "2 ") -> "Transfer 2"
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
    val isMetro = isMetroRoute(segment.routeName)
    val vehicleText = if (isMetro) {
        " :هتركب مترو"
    } else {
        " :هتركب باص"
    }
    val vehicleColor = if (isMetro) {
        Color(0xFF0F766E)
    } else {
        Color(0xFFFF7A00)
    }
    val vehicleBackground = if (isMetro) {
        Color(0x1F0F766E)
    } else {
        Color(0xFFFFF4E6)
    }

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
                    .background(vehicleColor)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(vehicleBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMetro) Icons.Default.Train else Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = vehicleColor,
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
                    color = vehicleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = vehicleText,
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
                        colors = gradientColors()
                    )
                )
        )

        TelegramIcon()
    }
}

private fun calculateTotalCost(segments: List<RouteDetail>): Int {
    var total = 0
    var index = 0
    while (index < segments.size) {
        total += segments[index].ticketPrice
        index++
    }
    return total
}

private fun calculateTotalTime(segments: List<RouteDetail>): Int {
    var total = 0
    var index = 0
    while (index < segments.size) {
        total += segments[index].averageTimeInMinutes
        index++
    }
    return total
}

private fun getEndStation(destination: String, segments: List<RouteDetail>): String {
    if (destination.length > 0) {
        return destination
    }

    if (segments.size == 0) {
        return ""
    }

    val lastSegment = segments[segments.size - 1]
    val stations = lastSegment.stations

    if (stations.size == 0) {
        return ""
    }

    return stations[stations.size - 1]
}

private fun startsWithText(value: String, prefix: String): Boolean {
    if (value.length < prefix.length) {
        return false
    }

    var index = 0
    while (index < prefix.length) {
        if (value[index] != prefix[index]) {
            return false
        }
        index++
    }

    return true
}

private fun isMetroRoute(routeName: String): Boolean {
    return hasTextPart(routeName, "الخط")
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

private fun gradientColors(): java.util.ArrayList<Color> {
    val colors = java.util.ArrayList<Color>()
    colors.add(Color(0xFF22C55E))
    colors.add(Color(0xFFFF7A00))
    return colors
}
