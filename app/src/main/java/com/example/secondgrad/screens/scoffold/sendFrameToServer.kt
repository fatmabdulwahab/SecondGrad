package com.example.secondgrad.screens.scoffold


import androidx.camera.core.ImageProxy

fun sendFrameToServer(image: ImageProxy) {

    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    // هنا تبعتي للـ API
    // Retrofit / WebSocket / gRPC

    println("Frame sent: ${bytes.size}")
}