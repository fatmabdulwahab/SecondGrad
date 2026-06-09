package com.example.secondgrad.screens.busCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimeAndCost(
    totalCost: Int = 0,
    totalTimeInMinutes: Int = 0
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {

        Column(
            modifier = Modifier.padding(top = 2.dp),
        ) {
            Text(
                text = "التكلفة",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row {
                Text(
                    text = " جنيه",
                    color = Color.Gray,
                    fontSize = 12.sp,
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = totalCost.toString(),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x28FF7A00)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ConfirmationNumber,
                contentDescription = "time",
                tint = Color(0xFFFF7A00),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(30.dp))

        VerticalDivider(
            modifier = Modifier.height(40.dp).padding(top = 12.dp)
        )

        Spacer(modifier = Modifier.width(30.dp))

        Column(
            modifier = Modifier.padding(top = 2.dp),
        ) {
            Text(
                text = "الوقت الاجمالى",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row {
                Text(
                    text = " دقيقة",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 18.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = totalTimeInMinutes.toString(),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x28FF7A00)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "time",
                tint = Color(0xFFFF7A00)
            )
        }
    }
}