package com.example.secondgrad.screens.scoffold

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VoiceCameraSearch(
    onMicClick: () -> Unit = {},
    onCameraGranted: () -> Unit = {}
) {

    // Permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onCameraGranted()
        } else {
            // هنا ممكن تحطي Toast أو رسالة
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(9.dp)
            .background(Color.White, RoundedCornerShape(30.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(30.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color.Green, CircleShape)
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = "Idle",
            color = Color.Gray
        )

        Spacer(Modifier.weight(1f))

        IconButton(onClick = onMicClick) {
            Icon(
                imageVector = Icons.Outlined.Mic,
                contentDescription = "Mic",
                tint = Color(0xFF2EAD60)
            )
        }

        IconButton(onClick = {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Camera",
                tint = Color(0xFF2EAD60)
            )
        }
    }
}