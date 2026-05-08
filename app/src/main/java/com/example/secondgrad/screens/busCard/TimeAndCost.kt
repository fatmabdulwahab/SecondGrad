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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.secondgrad.R

@Composable
fun TimeAndCost( ) {

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
                    text =  "50",
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
                painter = painterResource(id = R.drawable.img),
                contentDescription = "time",
                tint = Color.Unspecified,
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
                    text = "50",
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
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "time",
                tint = Color.Unspecified
            )
        }
    }
}