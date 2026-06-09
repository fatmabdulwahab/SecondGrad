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

        if (!MediaPipeNativeSupport.isAvailable()) {
            Log.e("HAND_NATIVE_MISSING", MediaPipeNativeSupport.unavailableMessage())
            handLandmarker = null
            return false
        }

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
            logHandLoadError("HAND_ASSET_LOAD", throwable)
            handLandmarker = null
            false
        }
    }

    private fun tryCreateFromCachedFile(): Boolean {
        val modelFile = HandModelProvider.prepareModelFile(context) ?: return false
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
            logHandLoadError("HAND_FILE_LOAD", throwable)
            handLandmarker = null
            false
        }
    }

    private fun logHandLoadError(tag: String, throwable: Throwable) {
        if (isNativeLibraryError(throwable)) {
            Log.e("HAND_NATIVE_MISSING", MediaPipeNativeSupport.unavailableMessage())
        } else {
            Log.e(tag, throwable.message.toString())
        }
    }

    private fun isNativeLibraryError(throwable: Throwable): Boolean {
        if (throwable is UnsatisfiedLinkError) {
            return true
        }

        val message = throwable.message ?: ""
        if (containsNativeLibraryHint(message)) {
            return true
        }

        val cause = throwable.cause
        return cause != null && isNativeLibraryError(cause)
    }

    private fun containsNativeLibraryHint(message: String): Boolean {
        return com.example.secondgrad.containsText(message, "libmediapipe_tasks") ||
            com.example.secondgrad.containsText(message, "UnsatisfiedLinkError") ||
            com.example.secondgrad.containsText(message, "dlopen failed")
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
