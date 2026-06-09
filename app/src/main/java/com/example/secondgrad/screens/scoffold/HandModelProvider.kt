package com.example.secondgrad.screens.scoffold

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

object HandModelProvider {

    private const val MODEL_FILE_NAME = "hand_landmarker.task"
    private const val MIN_MODEL_BYTES = 1_000_000L

    fun isModelAvailable(context: Context): Boolean {
        return prepareModelFile(context) != null
    }

    fun openModelBuffer(context: Context): java.nio.MappedByteBuffer? {
        val modelFile = prepareModelFile(context) ?: return null

        return try {
            RandomAccessFile(modelFile, "r").channel.map(
                FileChannel.MapMode.READ_ONLY,
                0,
                modelFile.length()
            )
        } catch (throwable: Throwable) {
            Log.e("HAND_MODEL", throwable.message.toString())
            null
        }
    }

    private fun prepareModelFile(context: Context): File? {
        val modelFile = File(context.filesDir, MODEL_FILE_NAME)

        if (modelFile.exists() && modelFile.length() >= MIN_MODEL_BYTES) {
            return modelFile
        }

        return try {
            context.assets.open(MODEL_FILE_NAME).use { input ->
                FileOutputStream(modelFile).use { output ->
                    val buffer = ByteArray(8192)
                    var read = input.read(buffer)
                    while (read > 0) {
                        output.write(buffer, 0, read)
                        read = input.read(buffer)
                    }
                }
            }

            if (modelFile.exists() && modelFile.length() >= MIN_MODEL_BYTES) {
                modelFile
            } else {
                modelFile.delete()
                null
            }
        } catch (throwable: Throwable) {
            Log.e("HAND_MODEL", throwable.message.toString())
            if (modelFile.exists()) {
                modelFile.delete()
            }
            null
        }
    }
}
