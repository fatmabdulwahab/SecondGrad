package com.example.secondgrad.screens.scoffold

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer

object HandModelProvider {

    const val MODEL_FILE_NAME = "hand_landmarker.task"
    private const val MIN_MODEL_BYTES = 1_000_000L
    private const val MODEL_DOWNLOAD_URL =
        "https://storage.googleapis.com/mediapipe-models/hand_landmarker/hand_landmarker/float16/1/hand_landmarker.task"

    private val httpClient = OkHttpClient()

    fun prepareModelFile(context: Context): File? {
        return ensureModelFileBlocking(context)
    }

    fun isModelAvailable(context: Context): Boolean {
        val cachedFile = File(context.filesDir, MODEL_FILE_NAME)
        if (cachedFile.exists() && cachedFile.length() >= MIN_MODEL_BYTES) {
            return true
        }
        return isAssetPackaged(context)
    }

    fun hasBundledAsset(context: Context): Boolean {
        return isAssetPackaged(context)
    }

    fun readModelDirectBuffer(modelFile: File): ByteBuffer? {
        if (!modelFile.exists() || modelFile.length() < MIN_MODEL_BYTES) {
            return null
        }

        var input: FileInputStream? = null
        return try {
            val fileSize = modelFile.length()
            if (fileSize > Int.MAX_VALUE) {
                return null
            }

            val buffer = ByteBuffer.allocateDirect(fileSize.toInt())
            input = FileInputStream(modelFile)
            val chunk = ByteArray(8192)
            var read = input.read(chunk)
            while (read > 0) {
                buffer.put(chunk, 0, read)
                read = input.read(chunk)
            }
            buffer.flip()
            buffer
        } catch (throwable: Throwable) {
            Log.e("HAND_MODEL", throwable.message.toString())
            null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (closeError: Throwable) {
                    Log.e("HAND_MODEL", closeError.message.toString())
                }
            }
        }
    }

    private fun ensureModelFileBlocking(context: Context): File? {
        val cachedFile = File(context.filesDir, MODEL_FILE_NAME)
        if (cachedFile.exists() && cachedFile.length() >= MIN_MODEL_BYTES) {
            return cachedFile
        }

        if (isAssetPackaged(context)) {
            try {
                copyAssetToFile(context, MODEL_FILE_NAME, cachedFile)
                if (cachedFile.exists() && cachedFile.length() >= MIN_MODEL_BYTES) {
                    return cachedFile
                }
            } catch (throwable: Throwable) {
                Log.e("HAND_MODEL", throwable.message.toString())
            }
        }

        val downloaded = downloadModelFile(cachedFile)
        if (downloaded != null && downloaded.exists() && downloaded.length() >= MIN_MODEL_BYTES) {
            return downloaded
        }

        if (cachedFile.exists()) {
            cachedFile.delete()
        }
        return null
    }

    private fun downloadModelFile(targetFile: File): File? {
        val tempFile = File(targetFile.parentFile, "${MODEL_FILE_NAME}.download")
        if (tempFile.exists()) {
            tempFile.delete()
        }

        var output: FileOutputStream? = null
        return try {
            val request = Request.Builder()
                .url(MODEL_DOWNLOAD_URL)
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return null
            }

            val body = response.body ?: return null
            output = FileOutputStream(tempFile)

            val source = body.byteStream()
            val buffer = ByteArray(8192)
            var read = source.read(buffer)
            while (read > 0) {
                output.write(buffer, 0, read)
                read = source.read(buffer)
            }
            output.flush()

            if (targetFile.exists()) {
                targetFile.delete()
            }

            if (!tempFile.renameTo(targetFile)) {
                copyFile(tempFile, targetFile)
                tempFile.delete()
            }

            if (targetFile.exists() && targetFile.length() >= MIN_MODEL_BYTES) {
                targetFile
            } else {
                targetFile.delete()
                null
            }
        } catch (throwable: Throwable) {
            Log.e("HAND_MODEL", throwable.message.toString())
            tempFile.delete()
            null
        } finally {
            if (output != null) {
                try {
                    output.close()
                } catch (closeError: Throwable) {
                    Log.e("HAND_MODEL", closeError.message.toString())
                }
            }
        }
    }

    private fun isAssetPackaged(context: Context): Boolean {
        var input: InputStream? = null
        return try {
            input = context.assets.open(MODEL_FILE_NAME)
            var totalBytes = 0L
            val buffer = ByteArray(8192)
            var read = input.read(buffer)
            while (read > 0) {
                totalBytes = totalBytes + read.toLong()
                if (totalBytes >= MIN_MODEL_BYTES) {
                    return true
                }
                read = input.read(buffer)
            }
            totalBytes >= MIN_MODEL_BYTES
        } catch (throwable: Throwable) {
            false
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (closeError: Throwable) {
                    Log.e("HAND_MODEL", closeError.message.toString())
                }
            }
        }
    }

    private fun copyAssetToFile(context: Context, assetName: String, targetFile: File) {
        var input: InputStream? = null
        var output: FileOutputStream? = null

        try {
            input = context.assets.open(assetName)
            output = FileOutputStream(targetFile)

            val buffer = ByteArray(8192)
            var bytesRead = input.read(buffer)
            while (bytesRead > 0) {
                output.write(buffer, 0, bytesRead)
                bytesRead = input.read(buffer)
            }
            output.flush()
        } finally {
            if (output != null) {
                try {
                    output.close()
                } catch (closeError: Throwable) {
                    Log.e("HAND_MODEL", closeError.message.toString())
                }
            }

            if (input != null) {
                try {
                    input.close()
                } catch (closeError: Throwable) {
                    Log.e("HAND_MODEL", closeError.message.toString())
                }
            }
        }
    }

    private fun copyFile(source: File, destination: File) {
        var input: FileInputStream? = null
        var output: FileOutputStream? = null

        try {
            input = FileInputStream(source)
            output = FileOutputStream(destination)
            val buffer = ByteArray(8192)
            var read = input.read(buffer)
            while (read > 0) {
                output.write(buffer, 0, read)
                read = input.read(buffer)
            }
            output.flush()
        } finally {
            if (output != null) {
                try {
                    output.close()
                } catch (closeError: Throwable) {
                    Log.e("HAND_MODEL", closeError.message.toString())
                }
            }

            if (input != null) {
                try {
                    input.close()
                } catch (closeError: Throwable) {
                    Log.e("HAND_MODEL", closeError.message.toString())
                }
            }
        }
    }
}
