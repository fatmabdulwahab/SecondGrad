package com.example.secondgrad.screens.busCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
@Preview(showSystemUi = true)
fun Circle() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .border(
                BorderStroke(2.dp, color = Color(0xFFFF7A00)),
                CircleShape
            )
            .clip(CircleShape)
            .background(Color.White)
    )
}