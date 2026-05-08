package com.example.secondgrad.screens.scoffold

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class FrameAnalyzer(
    val onFrame: (ImageProxy) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        onFrame(image)
        image.close()
    }
}