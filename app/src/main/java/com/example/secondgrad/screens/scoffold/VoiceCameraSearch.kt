package com.example.secondgrad.screens.scoffold



import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VoiceCameraSearch(
    onVoiceResult: (String) -> Unit,
    onOpenCamera: () -> Unit
) {
    var state by remember { mutableStateOf("Stopped") }
    var showRecorder by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(9.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RecorderControls(
            state = state,
            onMicClick = {
                showRecorder = true
                state = "Recording..."
                startVoiceRecognition(context) { result ->
                    state = "Stopped"
                    onVoiceResult(result)
                }
            },
            onCameraClick = {
                showRecorder = true
                onOpenCamera()
            },
            onSendClick = {
                state = "Stopped"
            },
            onDeleteClick = {
                showRecorder = false
                state = "Stopped"
            }
        )

        if (showRecorder) {
            AudioPreviewBar()
        }
    }
}

@Composable
private fun RecorderControls(
    state: String,
    onMicClick: () -> Unit,
    onCameraClick: () -> Unit,
    onSendClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(32.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(0xFF22C55E), CircleShape)
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = state,
            color = Color(0xFF374151),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.weight(1f))

        CircleIconButton(
            onClick = onMicClick,
            backgroundColor = Color(0xFFE0F8EA),
            borderColor = Color.Transparent
        ) {
            Icon(
                imageVector = Icons.Outlined.Mic,
                contentDescription = "Mic",
                tint = Color(0xFF2EAD60)
            )
        }

        Spacer(Modifier.width(8.dp))

        CircleIconButton(
            onClick = onCameraClick,
            backgroundColor = Color.White,
            borderColor = Color(0xFF22C55E)
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Camera",
                tint = Color(0xFF2EAD60)
            )
        }

        Spacer(Modifier.width(8.dp))

        Button(
            onClick = onSendClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
            shape = RoundedCornerShape(22.dp),
            contentPadding = ButtonDefaults.ContentPadding,
            modifier = Modifier.height(42.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Send",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.width(8.dp))

        CircleIconButton(
            onClick = onDeleteClick,
            backgroundColor = Color(0xFFF3F4F6),
            borderColor = Color.Transparent
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                tint = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    borderColor: Color,
    content: @Composable () -> Unit
) {
    val modifier = if (borderColor == Color.Transparent) {
        Modifier
            .size(42.dp)
            .background(backgroundColor, CircleShape)
    } else {
        Modifier
            .size(42.dp)
            .background(backgroundColor, CircleShape)
            .border(1.dp, borderColor, CircleShape)
    }

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
private fun AudioPreviewBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFF4B4B4B), RoundedCornerShape(22.dp))
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "Play",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = "0:00 / 0:03",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .background(Color(0xFF9CA3AF), RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(6.dp)
                    .background(Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
            )
        }

        Spacer(Modifier.width(16.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Volume",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "More",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
