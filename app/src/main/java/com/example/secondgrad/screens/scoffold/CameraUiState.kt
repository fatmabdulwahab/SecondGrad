package com.example.secondgrad.screens.scoffold

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CameraUiState {
    Initial,
    CreatingSession,
    ConnectingServer,
    StartingCamera,
    Ready
}

fun cameraUiStateMessage(state: CameraUiState): String {
    return when (state) {
        CameraUiState.Initial -> "كاميرا الترجمة جاهزة للتشغيل"
        CameraUiState.CreatingSession -> "الخطوة 1 من 4: جاري إنشاء جلسة الترجمة..."
        CameraUiState.ConnectingServer -> "الخطوة 2 من 4: جاري تحميل ملف التعرف على الإشارة..."
        CameraUiState.StartingCamera -> "الخطوة 3 من 4: جاري تشغيل الكاميرا..."
        CameraUiState.Ready -> "الخطوة 4 من 4: الكاميرا جاهزة"
    }
}

@Composable
fun CameraStepIndicator(currentState: CameraUiState) {
    if (currentState == CameraUiState.Initial || currentState == CameraUiState.Ready) {
        return
    }

    val stepIndex = when (currentState) {
        CameraUiState.CreatingSession -> 1
        CameraUiState.ConnectingServer -> 2
        CameraUiState.StartingCamera -> 3
        else -> 0
    }

    if (stepIndex == 0) {
        return
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        var step = 1
        while (step <= 3) {
            val color = if (step <= stepIndex) Color(0xFF10B981) else Color(0xFF475569)
            Box(
                modifier = Modifier
                    .size(if (step == stepIndex) 10.dp else 8.dp)
                    .background(color, CircleShape)
            )
            step = step + 1
        }
    }
}

@Composable
fun InitialStateView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .border(1.dp, Color(0xFF00875A), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = null,
                tint = Color(0xFF00875A),
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "كاميرا الترجمة جاهزة للتشغيل",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "يرجى الضغط على \"بدء ترجمة الإشارات\" والسماح بصلاحية الكاميرا لبدء فك شفرات الحروف العربية",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun LoadingStateView(statusText: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF00875A),
            strokeWidth = 3.dp,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = statusText,
            color = Color.LightGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}