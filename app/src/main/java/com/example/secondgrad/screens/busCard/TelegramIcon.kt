package com.example.secondgrad.screens.busCard


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.secondgrad.R

@Composable
@Preview(showSystemUi = true)
fun TelegramIcon() {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(Color(0xFF22C55E))
    ) {

        Icon(
            painter = painterResource(id = R.drawable.telegram),
            contentDescription = "telegram",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(12.dp)
        )
    }
}
