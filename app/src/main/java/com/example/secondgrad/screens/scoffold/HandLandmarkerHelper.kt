package com.example.secondgrad.screens.scoffold

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.secondgrad.RecognizeRequest
import com.example.secondgrad.RetrofitInstance
import com.example.secondgrad.SignViewModel
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HandLandmarkerHelper(
    private val context: Context,

    private val onLandmarksDetected: (List<Float>) -> Unit
) {

    private var handLandmarker: HandLandmarker? = null
    var sessionId: String? = null // 👈 جعلناه public عشان الـ UI يقدر يقرأه عند إنهاء الجلسة
    private var lastSentTime = 0L

    fun setupHandLandmarker() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("hand_landmarker.task")
                .build()

            val options = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener(this::onResult)
                .setErrorListener { Log.e("HAND_ERROR", it.message.toString()) }
                .build()

            handLandmarker = HandLandmarker.createFromOptions(context, options)

            Log.d("HAND", "landmarker created = ${handLandmarker != null}")

        } catch (e: Exception) {
            Log.e("HAND_FATAL", e.message.toString())
        }
    }
    fun getHandLandmarker(): HandLandmarker? = handLandmarker



    private fun onResult(result: HandLandmarkerResult, inputImage: MPImage) {

        if (result.landmarks().isEmpty()) return
        val now = System.currentTimeMillis()
        if (now - lastSentTime < 1000) return // إرسال فريم كل ثانية
        lastSentTime = now


        val firstHand = result.landmarks()[0]
        val landmarks = mutableListOf<Float>()

        for (point in firstHand) {
            landmarks.add(point.x())
            landmarks.add(point.y())
            landmarks.add(point.z())
        }

        onLandmarksDetected(landmarks)
    }
}