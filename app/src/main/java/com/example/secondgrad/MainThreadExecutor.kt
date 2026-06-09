package com.example.secondgrad

import android.os.Handler
import android.os.Looper

object MainThreadExecutor {

    private val mainHandler = Handler(Looper.getMainLooper())

    fun <T> runInBackground(
        backgroundWork: () -> T,
        onResult: (T) -> Unit
    ) {
        Thread {
            val result = backgroundWork()
            mainHandler.post {
                onResult(result)
            }
        }.start()
    }

    fun runInBackground(
        backgroundWork: () -> Unit,
        onComplete: () -> Unit
    ) {
        Thread {
            backgroundWork()
            mainHandler.post {
                onComplete()
            }
        }.start()
    }

    fun postDelayed(delayMs: Long, action: () -> Unit) {
        mainHandler.postDelayed(action, delayMs)
    }

    fun runOnMain(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            mainHandler.post(action)
        }
    }
}
