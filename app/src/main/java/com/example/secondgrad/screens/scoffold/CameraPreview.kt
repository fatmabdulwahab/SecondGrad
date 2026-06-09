package com.example.secondgrad.screens.scoffold

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.secondgrad.MainThreadExecutor
import com.example.secondgrad.SignViewModel
import com.google.mediapipe.framework.image.BitmapImageBuilder
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun CameraPreview(
    activity: ComponentActivity,
    signViewModel: SignViewModel,
    modelPrepared: Boolean = false,
    onError: (String) -> Unit,
    onHandModelStatus: (Boolean) -> Unit = {}
) {
    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }
    var isCameraBound by remember { mutableStateOf(false) }
    var isHandReady by remember { mutableStateOf(false) }
    var cameraBindError by remember { mutableStateOf<String?>(null) }

    val handLandmarkerHelper = remember(signViewModel) {
        HandLandmarkerHelper(
            context = activity,
            onLandmarksDetected = { landmarks ->
                signViewModel.recognize(landmarks)
            }
        )
    }

    DisposableEffect(modelPrepared) {
        val cancelled = AtomicBoolean(false)

        MainThreadExecutor.runInBackground(
            backgroundWork = {
                if (!modelPrepared) {
                    HandModelProvider.prepareModelFile(activity)
                }
                handLandmarkerHelper.setupHandLandmarker()
            },
            onResult = { ready ->
                if (!cancelled.get()) {
                    isHandReady = ready
                    onHandModelStatus(ready)
                }
            }
        )

        onDispose {
            cancelled.set(true)
            handLandmarkerHelper.close()
            cameraExecutor.shutdown()
        }
    }

    if (cameraBindError != null) {
        val message = cameraBindError
        if (message != null) {
            onError(message)
            cameraBindError = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B141A))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        if (
                            ActivityCompat.checkSelfPermission(
                                ctx,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            cameraBindError = "اسمحي بصلاحية الكاميرا الأول"
                            return@addListener
                        }

                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build()
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            try {
                                if (handLandmarkerHelper.isReady()) {
                                    val bitmap = imageProxy.toBitmap()
                                    val mpImage = BitmapImageBuilder(bitmap).build()
                                    val result = handLandmarkerHelper.detectHands(mpImage)
                                    if (result != null) {
                                        handLandmarkerHelper.publishLandmarks(result)
                                    }
                                }
                            } catch (throwable: Throwable) {
                                Log.e("CAMERA_ANALYZER_ERROR", throwable.message.toString())
                            } finally {
                                imageProxy.close()
                            }
                        }

                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        cameraProvider.unbindAll()

                        val selectors = arrayOf(
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            CameraSelector.DEFAULT_BACK_CAMERA
                        )

                        var bound = false
                        var selectorIndex = 0
                        while (selectorIndex < selectors.size && !bound) {
                            try {
                                cameraProvider.bindToLifecycle(
                                    activity,
                                    selectors[selectorIndex],
                                    preview,
                                    imageAnalysis
                                )
                                bound = true
                                isCameraBound = true
                            } catch (bindError: Throwable) {
                                Log.e(
                                    "CAMERA_BIND_ERROR",
                                    "${selectors[selectorIndex]}: ${bindError.message}"
                                )
                            }
                            selectorIndex = selectorIndex + 1
                        }

                        if (!bound) {
                            cameraBindError = "مش قادرين نشغل الكاميرا على الجهاز ده"
                        }
                    } catch (throwable: Throwable) {
                        Log.e("CAMERA_BIND_ERROR", throwable.message.toString())
                        cameraBindError = "مش قادرين نشغل الكاميرا على الجهاز ده"
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        if (!isCameraBound) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        if (!isHandReady && isCameraBound) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "جاري تفعيل التعرف على الإشارة...",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color(0xCC000000))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
