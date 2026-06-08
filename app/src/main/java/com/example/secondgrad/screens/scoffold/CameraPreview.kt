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
import androidx.compose.runtime.LaunchedEffect
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
    signViewModel: SignViewModel
) {

    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

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
        handLandmarkerHelper.setupHandLandmarker()

        // إنشاء Session مرة واحدة
        signViewModel.createSession()
    }


    AndroidView(
        factory = { ctx ->

            val previewView = PreviewView(ctx)

            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({

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

                    val bitmap =
                        imageProxy.toBitmap()

                    val mpImage =
                        BitmapImageBuilder(bitmap).build()

                    val frameTime =
                        System.currentTimeMillis()

                    handLandmarkerHelper
                        .getHandLandmarker()
                        ?.detectAsync(
                            mpImage,
                            frameTime
                        )

                    imageProxy.close()
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

                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.CAMERA),
                        100
                    )

                    return@addListener
                }

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    activity,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}