package com.example.secondgrad.screens.busCard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ChipItem(text: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF22C55E) else Color(0xFF22C55E),
        modifier = Modifier
            .padding(4.dp)
            .clickable { }
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            fontSize = 12.sp
        )
    }
}
/*
Row (
                modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                 Column(
                     verticalArrangement = Arrangement.SpaceBetween
                 ) {

                     Text(text = "انت هنا",
                         modifier = Modifier
                             .align(Alignment.End),
                         color =Color(0xFFFF7A00),
                         fontWeight = FontWeight.Bold
                     )

                     Text(text = "جامعة القاهرة",
                         modifier = Modifier
                             .align(Alignment.End),
                         fontWeight = FontWeight.Bold
                     )

                 }
            }
 */