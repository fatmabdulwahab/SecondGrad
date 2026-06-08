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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.secondgrad.SignViewModel
import com.google.mediapipe.framework.image.BitmapImageBuilder
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    activity: ComponentActivity,
    signViewModel: SignViewModel,
    onError: (String) -> Unit
) {

    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }
    val isHandReady = remember { mutableStateOf(false) }

    val handLandmarkerHelper = remember(signViewModel) {
        HandLandmarkerHelper(
            context = activity,
            onLandmarksDetected = { landmarks ->

                Log.d("TEST_FLOW", "before viewModel call")

                signViewModel.recognize(landmarks)

                Log.d("TEST_FLOW", "after viewModel call")
            }
        )
    }


    LaunchedEffect(Unit) {
        val isReady = handLandmarkerHelper.setupHandLandmarker()
        isHandReady.value = isReady
        if (!isReady) {
            onError("ملف تشغيل الكاميرا غير موجود أو غير صالح")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            handLandmarkerHelper.close()
            cameraExecutor.shutdown()
        }
    }

    if (!isHandReady.value) {
        return
    }

    AndroidView(
        factory = { ctx ->

            val previewView = PreviewView(ctx)

            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({

                try {
                    val cameraProvider =
                        cameraProviderFuture.get()

                    val preview =
                        Preview.Builder().build()

                    val imageAnalysis =
                        ImageAnalysis.Builder()
                            .setBackpressureStrategy(
                                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                            )
                            .build()

                    imageAnalysis.setAnalyzer(
                        cameraExecutor
                    ) { imageProxy ->
                        try {
                            val handLandmarker = handLandmarkerHelper.getHandLandmarker()
                            if (handLandmarker == null) {
                                return@setAnalyzer
                            }

                            val bitmap =
                                imageProxy.toBitmap()

                            val mpImage =
                                BitmapImageBuilder(bitmap).build()

                            val frameTime =
                                System.currentTimeMillis()

                            handLandmarker.detectAsync(
                                mpImage,
                                frameTime
                            )
                        } catch (throwable: Throwable) {
                            Log.e("CAMERA_ANALYZER_ERROR", throwable.message.toString())
                        } finally {
                            imageProxy.close()
                        }
                    }

                    val cameraSelector =
                        CameraSelector.DEFAULT_BACK_CAMERA

                    preview.setSurfaceProvider(
                        previewView.surfaceProvider
                    )

                    if (
                        ActivityCompat.checkSelfPermission(
                            ctx,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        onError("اسمحي بصلاحية الكاميرا الأول")
                        return@addListener
                    }

                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        activity,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (throwable: Throwable) {
                    Log.e("CAMERA_BIND_ERROR", throwable.message.toString())
                    onError("مش قادرين نشغل الكاميرا على الجهاز ده")
                }

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}