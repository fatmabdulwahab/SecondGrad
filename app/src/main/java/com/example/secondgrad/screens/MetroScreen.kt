package com.example.secondgrad.screens

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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.secondgrad.R
import com.example.secondgrad.screens.busCard.ChipItem
import com.example.secondgrad.screens.busCard.Circle
import com.example.secondgrad.screens.busCard.SmallLine
import com.example.secondgrad.screens.busCard.TelegramIcon
import com.example.secondgrad.screens.busCard.TimeAndCost
import com.example.secondgrad.screens.busCard.VerticalLine

@Composable
@Preview(showSystemUi = true)
fun MetroScreen( ) {
    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(top = 50.dp , start = 12.dp,end = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )

            {

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Transfer badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x3322C55E))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "Transfer 1",
                            color = Color(0xFF14793A),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Route chips
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "خطة الرحلة",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,

                            )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ChipItem("M8", false)

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "<",
                                color = Color(0xFFFF7A00),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            ChipItem("M20", true)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    Modifier, DividerDefaults.Thickness,
                    DividerDefaults.color
                )

                Spacer(modifier = Modifier.height(12.dp))

                TimeAndCost( )

                HorizontalDivider(
                    Modifier, DividerDefaults.Thickness,
                    DividerDefaults.color
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Top // لضمان بدء العناصر من الأعلى معاً
                ) {
                    // Column النصوص والكروت
                    Column(
                        modifier = Modifier
                            .weight(1f) // هذا السطر هو الحل: يجعله يأخذ المساحة المتبقية فقط
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "انت هنا",
                            color = Color(0xFFFF7A00),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.End)
                        )

                        Text(
                            text = "جامعة القاهرة",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.End)
                        )

                        // الكارت الأول
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(0.5.dp, Color.LightGray),
                            modifier = Modifier
                                .fillMaxWidth() // سيملأ فقط المساحة التي حددها الـ weight
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "M20",
                                        color = Color(0xFFFF7A00),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = " :هتركب", color = Color.Gray, fontSize = 13.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFFF4E6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                        contentDescription = null,
                                        tint = Color(0xFFFF7A00),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier.fillMaxHeight().width(6.dp)
                                        .background(Color(0xFFFF7A00))
                                )
                            }


                        }
                        Spacer(modifier = Modifier.height(4.dp))
//                        // الكارت الثاني
//                        Card(
//                            shape = RoundedCornerShape(12.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF4)),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 4.dp)
//                        ) {
//                            Row(
//                                modifier = Modifier.fillMaxWidth().padding(12.dp),
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.End
//                            ) {
//                                Column(horizontalAlignment = Alignment.End) {
//                                    Text(
//                                        text = ":هتحول فى ",
//                                        color = Color(0xFF14793A),
//                                        fontSize = 11.sp,
//                                        modifier = Modifier.align(Alignment.End)
//                                    )
//                                    Text(
//                                        text = "الطريق الدائري",
//                                        color = Color.Black,
//                                        fontSize = 13.sp,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                }
//                                Spacer(modifier = Modifier.width(12.dp))
//                                Box(
//                                    modifier = Modifier.size(32.dp).clip(CircleShape)
//                                        .background(Color(0xFF22C55E)),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Icon(
//                                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
//                                        contentDescription = null,
//                                        tint = Color.White,
//                                        modifier = Modifier.size(18.dp)
//                                    )
//                                }
//                            }
//
//                        }
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Card(
//                            shape = RoundedCornerShape(12.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            border = BorderStroke(0.5.dp, Color.LightGray),
//                            modifier = Modifier
//                                .fillMaxWidth() // سيملأ فقط المساحة التي حددها الـ weight
//                                .padding(vertical = 8.dp)
//                        ) {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(IntrinsicSize.Min),
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.End
//                            ) {
//                                Row(
//                                    modifier = Modifier.padding(
//                                        horizontal = 12.dp,
//                                        vertical = 8.dp
//                                    ),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text(
//                                        text = "M20",
//                                        color = Color(0xFFFF7A00),
//                                        fontSize = 14.sp,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                    Text(text = " :هتركب", color = Color.Gray, fontSize = 13.sp)
//                                }
//
//                                Box(
//                                    modifier = Modifier
//                                        .size(32.dp)
//                                        .clip(RoundedCornerShape(8.dp))
//                                        .background(Color(0xFFFFF4E6)),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Icon(
//                                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
//                                        contentDescription = null,
//                                        tint = Color(0xFFFF7A00),
//                                        modifier = Modifier.size(20.dp)
//                                    )
//                                }
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Box(
//                                    modifier = Modifier.fillMaxHeight().width(6.dp)
//                                        .background(Color(0xFFFF7A00))
//                                )
//                            }
//                        }
//                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "هتنزل",
                            color = Color(0xFF22C55E),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.End)
                        )

                        Text(
                            text = "فى التجمع الخامس",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Circle()
                        SmallLine()
                        TelegramIcon()
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth()
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