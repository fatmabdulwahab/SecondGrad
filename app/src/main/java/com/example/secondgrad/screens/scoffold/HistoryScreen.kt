package com.example.secondgrad.screens.scoffold

import android.R.attr.end
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sanitizer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.secondgrad.R

@Preview(showSystemUi = true)
@Composable
fun HistoryScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E7D52))
            .padding(16.dp)
    ) {
        Column (modifier = Modifier.padding(top = 30.dp)){

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 28.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // مسافة بين كل كارت والثاني
            ) {
                items(6) { // عرض 6 كروت
                    HistoryCard()
                }
            }

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),

                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_1),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

//                Icon(
//                    imageVector = Icons.Filled.CleaningServices,
//                    contentDescription = null,
//                    tint = Color(0xFF2E7D52),
//                    modifier = Modifier.size(16.dp)
//                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "مسح السجل",
                    color = Color(0xFF2E7D52),
                    fontWeight = FontWeight.Bold
                )


            }
        }
    }
}
@Composable
fun HistoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xD2F3F2F2))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(horizontalAlignment = Alignment.End) {
                    Text("من", color = Color.Gray, fontSize = 12.sp)
                    Text("جامعة القاهرة", fontWeight = FontWeight.Bold)
                }


                Text("←", color = Color.Blue, fontSize = 20.sp)


                Column(horizontalAlignment = Alignment.Start) {
                    Text("إلى", color = Color.Gray, fontSize = 12.sp)
                    Text("التجمع الخامس", fontWeight = FontWeight.Bold)
                }

            }

            Spacer(Modifier.height(8.dp))

            // =====================
            // 2) DATE SECTION
            // =====================
            Text(
                text = "2026-05-15",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(12.dp))

            // =====================
            // 3) BUTTONS SECTION
            // =====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {


                CustomIconButton(
                    onClick = { /* delete */ },
                    modifier = Modifier.padding(end = 6.dp)
                )

                Button(
                    onClick = { },
                    modifier = Modifier.height(35.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        Color(0xFF2AC769)
                    )
                ) {
                    Text("بحث جديد", fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Search,
                        null, Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}