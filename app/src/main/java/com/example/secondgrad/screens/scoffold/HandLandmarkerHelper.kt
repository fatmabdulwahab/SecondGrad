package com.example.secondgrad.screens.scoffold

import android.content.Context
import android.util.Log
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
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
        close()

        if (tryCreateFromBundledAsset()) {
            return true
        }

        if (tryCreateFromCachedFile()) {
            return true
        }

        handLandmarker = null
        return false
    }

    fun detectHands(mpImage: MPImage): HandLandmarkerResult? {
        val landmarker = handLandmarker ?: return null

        return try {
            landmarker.detect(mpImage, ImageProcessingOptions.builder().build())
        } catch (throwable: Throwable) {
            Log.e("HAND_DETECT", throwable.message.toString())
            null
        }
    }

    fun isReady(): Boolean {
        return handLandmarker != null
    }

    fun close() {
        try {
            handLandmarker?.close()
        } catch (throwable: Throwable) {
            Log.e("HAND_CLOSE_ERROR", throwable.message.toString())
        }
        handLandmarker = null
    }

    private fun tryCreateFromBundledAsset(): Boolean {
        if (!HandModelProvider.hasBundledAsset(context)) {
            return false
        }

        return try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(HandModelProvider.MODEL_FILE_NAME)
                .build()

            handLandmarker = HandLandmarker.createFromOptions(
                context,
                buildLandmarkerOptions(baseOptions)
            )

            handLandmarker != null
        } catch (throwable: Throwable) {
            Log.e("HAND_ASSET_LOAD", throwable.message.toString())
            handLandmarker = null
            false
        }
    }

    private fun tryCreateFromCachedFile(): Boolean {
        val modelFile = HandModelProvider.ensureModelFile(context) ?: return false
        val modelBuffer = HandModelProvider.readModelDirectBuffer(modelFile) ?: return false

        return try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetBuffer(modelBuffer)
                .build()

            handLandmarker = HandLandmarker.createFromOptions(
                context,
                buildLandmarkerOptions(baseOptions)
            )

            handLandmarker != null
        } catch (throwable: Throwable) {
            Log.e("HAND_FILE_LOAD", throwable.message.toString())
            handLandmarker = null
            false
        }
    }

    private fun buildLandmarkerOptions(baseOptions: BaseOptions): HandLandmarker.HandLandmarkerOptions {
        return HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.IMAGE)
            .setNumHands(1)
            .setMinHandDetectionConfidence(0.35f)
            .setMinHandPresenceConfidence(0.35f)
            .setMinTrackingConfidence(0.35f)
            .build()
    }

    fun publishLandmarks(result: HandLandmarkerResult) {
        if (result.landmarks().size == 0) {
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastSentTime < 1000) {
            return
        }
        lastSentTime = now

        val firstHand = result.landmarks()[0]
        val landmarks = java.util.ArrayList<Float>()

        for (point in firstHand) {
            landmarks.add(point.x())
            landmarks.add(point.y())
            landmarks.add(point.z())
        }

        onLandmarksDetected(landmarks)
    }
}
