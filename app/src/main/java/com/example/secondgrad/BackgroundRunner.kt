package com.example.secondgrad

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object BackgroundRunner {

    suspend fun <T> run(block: () -> T): T {
        return suspendCancellableCoroutine { continuation ->
            val worker = Thread {
                var result: T? = null
                var error: Throwable? = null

                try {
                    result = block()
                } catch (throwable: Throwable) {
                    error = throwable
                }

                Handler(Looper.getMainLooper()).post {
                    if (!continuation.isActive) {
                        return@post
                    }

                    if (error != null) {
                        continuation.resumeWith(Result.failure(error))
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        continuation.resume(result as T)
                    }
                }
            }
            worker.start()
        }
    }
}
