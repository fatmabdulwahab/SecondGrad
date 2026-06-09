package com.example.secondgrad.screens.scoffold

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile

private const val MAX_RECORD_SECONDS = 10
private const val MIN_RECORD_SECONDS = 3
private const val MIN_RECORD_BYTES = 48000L
private const val WAV_SAMPLE_RATE = 16000
private const val WAV_CHANNEL_COUNT = 1
private const val WAV_BITS_PER_SAMPLE = 16

private class RecordingFlag {
    @Volatile
    var active: Boolean = false
}

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
    val currentIsSending by rememberUpdatedState(isSending)
    val recordingFlag = remember { RecordingFlag() }
    var audioRecord by remember { mutableStateOf<AudioRecord?>(null) }
    var recordingThread by remember { mutableStateOf<Thread?>(null) }
    var recordedFile by remember { mutableStateOf<File?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var displaySeconds by remember { mutableIntStateOf(0) }
    var stoppedDurationSeconds by remember { mutableIntStateOf(0) }
    var recordingStartedAtMs by remember { mutableStateOf(0L) }
    var shouldStartAfterPermission by remember { mutableStateOf(false) }

    fun releaseRecorder() {
        val activeRecorder = audioRecord
        if (activeRecorder != null) {
            activeRecorder.release()
        }
        audioRecord = null
    }

    fun waitForRecordingThread() {
        val thread = recordingThread
        if (thread != null) {
            try {
                thread.join(2000)
            } catch (exception: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
        recordingThread = null
    }

    fun deleteRecordedFile() {
        val file = recordedFile
        if (file != null) {
            file.delete()
        }
        recordedFile = null
        displaySeconds = 0
        stoppedDurationSeconds = 0
        recordingStartedAtMs = 0L
    }

    fun currentDurationSeconds(): Int {
        if (recordingStartedAtMs > 0L) {
            return elapsedSecondsSince(recordingStartedAtMs)
        }
        return stoppedDurationSeconds
    }

    fun stopRecording() {
        if (audioRecord == null && !isRecording) {
            return
        }

        recordingFlag.active = false
        isRecording = false

        val activeRecorder = audioRecord
        if (activeRecorder != null) {
            try {
                activeRecorder.stop()
            } catch (exception: Exception) {
                deleteRecordedFile()
                releaseRecorder()
                recordingStartedAtMs = 0L
                Toast.makeText(context, "التسجيل قصير جدًا، جربي مرة تانية", Toast.LENGTH_SHORT).show()
                return
            }
        }

        waitForRecordingThread()
        stoppedDurationSeconds = currentDurationSeconds()
        displaySeconds = stoppedDurationSeconds
        releaseRecorder()
        recordingStartedAtMs = 0L
    }

    fun startRecording() {
        if (isSending) {
            return
        }

        recordingFlag.active = false
        waitForRecordingThread()
        releaseRecorder()
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
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
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

            recordingFlag.active = true
            recorder.startRecording()

            audioRecord = recorder
            recordedFile = outputFile
            recordingStartedAtMs = System.currentTimeMillis()
            displaySeconds = 0
            stoppedDurationSeconds = 0
            isRecording = true

            val thread = Thread {
                writeWavFile(
                    recorder = recorder,
                    file = outputFile,
                    keepRecording = recordingFlag,
                    bufferSize = bufferSize
                )
            }
            recordingThread = thread
            thread.start()
        } catch (exception: Exception) {
            recordingFlag.active = false
            outputFile.delete()
            releaseRecorder()
            Toast.makeText(context, "مش قادرين نبدأ التسجيل", Toast.LENGTH_SHORT).show()
        }
    }

    val recordPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && shouldStartAfterPermission) {
            startRecording()
        } else if (!isGranted) {
            Toast.makeText(context, "محتاجين صلاحية المايك عشان التسجيل", Toast.LENGTH_SHORT).show()
        }
        shouldStartAfterPermission = false
    }

    fun requestRecording() {
        if (isSending) {
            return
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            startRecording()
            return
        }

        shouldStartAfterPermission = true
        recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    LaunchedEffect(isRecording, recordingStartedAtMs) {
        while (isRecording && recordingStartedAtMs > 0L) {
            displaySeconds = elapsedSecondsSince(recordingStartedAtMs)
            if (displaySeconds >= MAX_RECORD_SECONDS) {
                stopRecording()
                break
            }
            delay(250)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recordingFlag.active = false
            waitForRecordingThread()

            if (isRecording) {
                val activeRecorder = audioRecord
                if (activeRecorder != null) {
                    try {
                        activeRecorder.stop()
                    } catch (exception: Exception) {
                        // Ignore stop errors while disposing the UI.
                    }
                }
            }

            releaseRecorder()

            val file = recordedFile
            if (file != null && !currentIsSending) {
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
                elapsedSeconds = displaySeconds,
                onStopClick = ::stopRecording
            )
        } else {
            StoppedControls(
                recordedFile = recordedFile,
                isSending = isSending,
                durationSeconds = stoppedDurationSeconds,
                onRecordAgain = { requestRecording() },
                onCameraClick = onCameraClick,
                onSendVoice = { file ->
                    val uploadCopy = copyVoiceFileForUpload(file, context.cacheDir)
                    if (uploadCopy != null) {
                        onSendVoice(uploadCopy)
                    } else {
                        Toast.makeText(context, "مش قادرين نجهّز التسجيل للإرسال", Toast.LENGTH_SHORT).show()
                    }
                },
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
    durationSeconds: Int,
    onRecordAgain: () -> Unit,
    onCameraClick: () -> Unit,
    onSendVoice: (File) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val canSend = recordedFile != null
        && !isSending
        && durationSeconds >= MIN_RECORD_SECONDS

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
            text = when {
                isSending -> "جاري إرسال التسجيل..."
                recordedFile == null -> "اضغطي المايك وقولي: من [مكان] إلى [مكان]"
                durationSeconds < MIN_RECORD_SECONDS -> "التسجيل قصير، سجّلي تاني"
                else -> "التسجيل جاهز للإرسال"
            },
            color = Color(0xFF1F2937),
            fontSize = if (recordedFile == null) 13.sp else 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onRecordAgain,
            enabled = !isSending,
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
            enabled = !isSending,
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
                val file = recordedFile
                if (file != null && isRecordingReady(file, durationSeconds, context)) {
                    onSendVoice(file)
                }
            },
            enabled = canSend,
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
            totalSeconds = clampSeconds(durationSeconds)
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

private fun formatSeconds(seconds: Int): String {
    return if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
}

private fun elapsedSecondsSince(startMs: Long): Int {
    val diff = System.currentTimeMillis() - startMs
    if (diff < 0L) {
        return 0
    }
    return (diff / 1000L).toInt()
}

private fun writeWavFile(
    recorder: AudioRecord,
    file: File,
    keepRecording: RecordingFlag,
    bufferSize: Int
) {
    val buffer = ByteArray(bufferSize)
    var audioBytesWritten = 0
    var randomAccessFile: RandomAccessFile? = null

    try {
        randomAccessFile = RandomAccessFile(file, "rw")
        writeWavHeader(randomAccessFile, 0)

        while (keepRecording.active) {
            val read = recorder.read(buffer, 0, buffer.size)
            if (read > 0) {
                randomAccessFile.write(buffer, 0, read)
                audioBytesWritten = audioBytesWritten + read
            }
        }

        updateWavHeader(randomAccessFile, audioBytesWritten)
    } catch (exception: Exception) {
        file.delete()
    } finally {
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close()
            } catch (closeError: Exception) {
                // Ignore close errors.
            }
        }
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

private fun copyVoiceFileForUpload(source: File, cacheDir: File): File? {
    val extension = if (source.name.endsWith(".wav")) ".wav" else ".m4a"
    val destination = File(cacheDir, "voice_upload_${System.currentTimeMillis()}$extension")
    var input: FileInputStream? = null
    var output: FileOutputStream? = null

    return try {
        input = FileInputStream(source)
        output = FileOutputStream(destination)
        val buffer = ByteArray(8192)
        var read = input.read(buffer)
        while (read > 0) {
            output.write(buffer, 0, read)
            read = input.read(buffer)
        }
        destination
    } catch (exception: Exception) {
        destination.delete()
        null
    } finally {
        if (input != null) {
            try {
                input.close()
            } catch (closeError: Exception) {
                // Ignore close errors.
            }
        }
        if (output != null) {
            try {
                output.close()
            } catch (closeError: Exception) {
                // Ignore close errors.
            }
        }
    }
}

private fun isRecordingReady(file: File, durationSeconds: Int, context: android.content.Context): Boolean {
    if (durationSeconds < MIN_RECORD_SECONDS) {
        Toast.makeText(
            context,
            "سجّلي على الأقل 3 ثواني واضحين قبل الإرسال",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    if (!file.exists() || file.length() < MIN_RECORD_BYTES) {
        Toast.makeText(
            context,
            "التسجيل ضعيف، جرّبي تسجّلي تاني بوضوح",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    if (!isValidWavFile(file)) {
        Toast.makeText(
            context,
            "ملف الصوت لسه بيتجهّز، استني ثانية وجربي تاني",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    return true
}

private fun isValidWavFile(file: File): Boolean {
    if (!file.name.endsWith(".wav")) {
        return true
    }

    if (file.length() < 44L) {
        return false
    }

    var input: FileInputStream? = null
    return try {
        input = FileInputStream(file)
        val header = ByteArray(12)
        val read = input.read(header)
        if (read < 12) {
            return false
        }

        val isRiff = header[0] == 'R'.code.toByte() &&
            header[1] == 'I'.code.toByte() &&
            header[2] == 'F'.code.toByte() &&
            header[3] == 'F'.code.toByte()
        val isWave = header[8] == 'W'.code.toByte() &&
            header[9] == 'A'.code.toByte() &&
            header[10] == 'V'.code.toByte() &&
            header[11] == 'E'.code.toByte()
        if (!isRiff || !isWave) {
            return false
        }

        val chunkSize = (header[4].toInt() and 0xff) or
            ((header[5].toInt() and 0xff) shl 8) or
            ((header[6].toInt() and 0xff) shl 16) or
            ((header[7].toInt() and 0xff) shl 24)
        val expectedSize = chunkSize + 8L
        val actualSize = file.length()
        actualSize >= expectedSize - 4L && actualSize <= expectedSize + 4L
    } catch (exception: Exception) {
        false
    } finally {
        if (input != null) {
            try {
                input.close()
            } catch (closeError: Exception) {
                // Ignore close errors.
            }
        }
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
