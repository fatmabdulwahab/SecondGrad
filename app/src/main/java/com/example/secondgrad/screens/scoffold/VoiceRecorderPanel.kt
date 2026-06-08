package com.example.secondgrad.screens.scoffold

import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.atomic.AtomicBoolean

private const val MAX_RECORD_SECONDS = 10
private const val WAV_SAMPLE_RATE = 16000
private const val WAV_CHANNEL_COUNT = 1
private const val WAV_BITS_PER_SAMPLE = 16

@SuppressLint("MissingPermission")
@Composable
fun VoiceRecorderPanel(
    isSending: Boolean,
    onSendVoice: (File) -> Unit,
    onCameraClick: () -> Unit,
    onDeleteRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val recordingFlag = remember { AtomicBoolean(false) }

    var audioRecord by remember { mutableStateOf<AudioRecord?>(null) }
    var recordingJob by remember { mutableStateOf<Job?>(null) }
    var recordedFile by remember { mutableStateOf<File?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    fun releaseRecorder() {
        val recorder = audioRecord
        if (recorder != null) {
            recorder.release()
        }
        audioRecord = null
    }

    fun deleteRecordedFile() {
        val file = recordedFile
        if (file != null) {
            file.delete()
        }
        recordedFile = null
        elapsedSeconds = 0
    }

    fun stopRecording() {
        recordingFlag.set(false)
        isRecording = false

        val recorder = audioRecord
        if (recorder != null) {
            try {
                if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    recorder.stop()
                }
            } catch (exception: Exception) {
                Toast.makeText(context, "التسجيل قصير جدًا، جربي مرة تانية", Toast.LENGTH_SHORT).show()
            }
        }

        releaseRecorder()
    }

    fun startRecording() {
        stopRecording()
        deleteRecordedFile()

        val minBufferSize = AudioRecord.getMinBufferSize(
            WAV_SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (minBufferSize <= 0) {
            Toast.makeText(context, "مش قادرين نجهز التسجيل على الجهاز ده", Toast.LENGTH_SHORT).show()
            return
        }

        val bufferSize = minBufferSize * 2
        val outputFile = File(context.cacheDir, "voice_${System.currentTimeMillis()}.wav")

        try {
            val recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                WAV_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                recorder.release()
                Toast.makeText(context, "مش قادرين نبدأ التسجيل", Toast.LENGTH_SHORT).show()
                return
            }

            recordingFlag.set(true)
            recorder.startRecording()

            audioRecord = recorder
            recordedFile = outputFile
            elapsedSeconds = 0
            isRecording = true

            recordingJob = scope.launch(Dispatchers.IO) {
                writeWavFile(
                    recorder = recorder,
                    file = outputFile,
                    keepRecording = recordingFlag,
                    bufferSize = bufferSize
                )
            }
        } catch (exception: Exception) {
            outputFile.delete()
            recordingFlag.set(false)
            releaseRecorder()
            Toast.makeText(context, "مش قادرين نبدأ التسجيل", Toast.LENGTH_SHORT).show()
        }
    }

    val recordPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startRecording()
        } else {
            Toast.makeText(context, "محتاجين صلاحية المايك عشان التسجيل", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            elapsedSeconds += 1

            if (elapsedSeconds >= MAX_RECORD_SECONDS) {
                stopRecording()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recordingFlag.set(false)
            recordingJob?.cancel()
            stopRecording()

            val file = recordedFile
            if (file != null) {
                file.delete()
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isRecording) {
            RecordingControls(
                elapsedSeconds = elapsedSeconds,
                onStopClick = ::stopRecording
            )
        } else {
            StoppedControls(
                recordedFile = recordedFile,
                isSending = isSending,
                elapsedSeconds = elapsedSeconds,
                onRecordAgain = { recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                onCameraClick = onCameraClick,
                onSendVoice = onSendVoice,
                onDelete = {
                    deleteRecordedFile()
                    onDeleteRecording()
                }
            )
        }
    }
}

@Composable
private fun RecordingControls(
    elapsedSeconds: Int,
    onStopClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .height(64.dp)
            .background(Color.White, RoundedCornerShape(32.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(32.dp))
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(0xFFF87171))
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Recording... 0:${formatSeconds(elapsedSeconds)}",
            color = Color(0xFF1F2937),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onStopClick,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF1F2))
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop recording",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun StoppedControls(
    recordedFile: File?,
    isSending: Boolean,
    elapsedSeconds: Int,
    onRecordAgain: () -> Unit,
    onCameraClick: () -> Unit,
    onSendVoice: (File) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .height(66.dp)
            .background(Color.White, RoundedCornerShape(34.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(34.dp))
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(0xFF22C55E))
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Stopped",
            color = Color(0xFF1F2937),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onRecordAgain,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFFDF5))
        ) {
            Icon(
                imageVector = Icons.Outlined.Mic,
                contentDescription = "Record again",
                tint = Color(0xFF16A34A)
            )
        }

        IconButton(
            onClick = onCameraClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFF22C55E), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Camera",
                tint = Color(0xFF16A34A)
            )
        }

        Button(
            onClick = {
                if (recordedFile != null) {
                    onSendVoice(recordedFile)
                }
            },
            enabled = recordedFile != null && !isSending,
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
            contentPadding = ButtonDefaults.ContentPadding,
            modifier = Modifier.height(44.dp)
        ) {
            if (isSending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Send", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        IconButton(
            onClick = onDelete,
            enabled = recordedFile != null && !isSending,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6))
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete recording",
                tint = Color(0xFF6B7280)
            )
        }
    }

    if (recordedFile != null) {
        Spacer(modifier = Modifier.height(14.dp))
        AudioPlaybackBar(
            file = recordedFile,
            totalSeconds = clampSeconds(elapsedSeconds)
        )
    }
}

@Composable
private fun AudioPlaybackBar(
    file: File,
    totalSeconds: Int
) {
    val context = LocalContext.current
    var mediaPlayer by remember(file) { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember(file) { mutableStateOf(false) }
    var progress by remember(file) { mutableFloatStateOf(0f) }

    fun stopPlayback() {
        val player = mediaPlayer
        if (player != null) {
            player.release()
        }
        mediaPlayer = null
        isPlaying = false
        progress = 0f
    }

    DisposableEffect(file) {
        onDispose { stopPlayback() }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            val player = mediaPlayer
            if (player == null || !player.isPlaying) {
                isPlaying = false
                progress = 0f
            } else {
                var duration = player.duration
                if (duration < 1) {
                    duration = 1
                }
                progress = player.currentPosition / duration.toFloat()
            }
            delay(250)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .height(42.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF525252))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconButton(
            onClick = {
                if (isPlaying) {
                    stopPlayback()
                } else {
                    try {
                        val newPlayer = MediaPlayer()
                        newPlayer.setDataSource(file.absolutePath)
                        newPlayer.prepare()
                        newPlayer.setOnCompletionListener {
                            stopPlayback()
                        }
                        newPlayer.start()

                        mediaPlayer = newPlayer
                        isPlaying = true
                    } catch (exception: Exception) {
                        Toast.makeText(context, "مش قادرين نشغل التسجيل", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play recording",
                tint = Color.White
            )
        }

        Text(
            text = "0:00 / 0:${formatSeconds(totalSeconds)}",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Color(0xFFD1D5DB),
            trackColor = Color(0xFF9CA3AF)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = null,
            tint = Color.White
        )

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            tint = Color.White
        )
    }
}

private fun writeWavFile(
    recorder: AudioRecord,
    file: File,
    keepRecording: AtomicBoolean,
    bufferSize: Int
) {
    val buffer = ByteArray(bufferSize)
    var audioBytesWritten = 0
    var randomAccessFile: RandomAccessFile? = null

    try {
        randomAccessFile = RandomAccessFile(file, "rw")
        writeWavHeader(randomAccessFile, 0)

        while (keepRecording.get()) {
            val read = recorder.read(buffer, 0, buffer.size)
            if (read > 0) {
                randomAccessFile.write(buffer, 0, read)
                audioBytesWritten += read
            }
        }

        updateWavHeader(randomAccessFile, audioBytesWritten)
    } catch (exception: Exception) {
        file.delete()
    } finally {
        randomAccessFile?.close()
    }
}

private fun writeWavHeader(file: RandomAccessFile, audioLength: Int) {
    file.setLength(0)
    file.writeBytes("RIFF")
    writeIntLittleEndian(file, 36 + audioLength)
    file.writeBytes("WAVE")
    file.writeBytes("fmt ")
    writeIntLittleEndian(file, 16)
    writeShortLittleEndian(file, 1)
    writeShortLittleEndian(file, WAV_CHANNEL_COUNT)
    writeIntLittleEndian(file, WAV_SAMPLE_RATE)
    writeIntLittleEndian(file, WAV_SAMPLE_RATE * WAV_CHANNEL_COUNT * WAV_BITS_PER_SAMPLE / 8)
    writeShortLittleEndian(file, WAV_CHANNEL_COUNT * WAV_BITS_PER_SAMPLE / 8)
    writeShortLittleEndian(file, WAV_BITS_PER_SAMPLE)
    file.writeBytes("data")
    writeIntLittleEndian(file, audioLength)
}

private fun updateWavHeader(file: RandomAccessFile, audioLength: Int) {
    file.seek(4)
    writeIntLittleEndian(file, 36 + audioLength)
    file.seek(40)
    writeIntLittleEndian(file, audioLength)
}

private fun writeIntLittleEndian(file: RandomAccessFile, value: Int) {
    file.write(value and 0xff)
    file.write(value shr 8 and 0xff)
    file.write(value shr 16 and 0xff)
    file.write(value shr 24 and 0xff)
}

private fun writeShortLittleEndian(file: RandomAccessFile, value: Int) {
    file.write(value and 0xff)
    file.write(value shr 8 and 0xff)
}

private fun formatSeconds(seconds: Int): String {
    return if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
}

private fun clampSeconds(seconds: Int): Int {
    if (seconds < 1) {
        return 1
    }
    if (seconds > MAX_RECORD_SECONDS) {
        return MAX_RECORD_SECONDS
    }
    return seconds
}
