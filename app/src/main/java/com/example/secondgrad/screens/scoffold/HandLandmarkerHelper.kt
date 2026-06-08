package com.example.secondgrad.screens.scoffold

import android.content.Context
import android.util.Log
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandLandmarkerHelper(
    private val context: Context,

    private val onLandmarksDetected: (List<Float>) -> Unit
) {

    private var handLandmarker: HandLandmarker? = null
    private var lastSentTime = 0L

    fun setupHandLandmarker(): Boolean {
        return try {
            val assetInputStream = context.assets.open("hand_landmarker.task")
            assetInputStream.close()

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

            handLandmarker != null
        } catch (throwable: Throwable) {
            Log.e("HAND_FATAL", throwable.message.toString())
            handLandmarker = null
            false
        }
    }

    fun getHandLandmarker(): HandLandmarker? = handLandmarker

    fun close() {
        try {
            handLandmarker?.close()
        } catch (throwable: Throwable) {
            Log.e("HAND_CLOSE_ERROR", throwable.message.toString())
        }
        handLandmarker = null
    }



    private fun onResult(result: HandLandmarkerResult, inputImage: MPImage) {

        if (result.landmarks().size == 0) return
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