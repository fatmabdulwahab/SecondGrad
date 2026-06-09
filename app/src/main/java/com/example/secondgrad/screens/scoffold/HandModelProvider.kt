package com.example.secondgrad.screens.scoffold

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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

        var randomAccessFile: RandomAccessFile? = null
        return try {
            randomAccessFile = RandomAccessFile(modelFile, "r")
            randomAccessFile.channel.map(
                FileChannel.MapMode.READ_ONLY,
                0,
                modelFile.length()
            )
        } catch (throwable: Throwable) {
            Log.e("HAND_MODEL", throwable.message.toString())
            null
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close()
                } catch (closeError: Throwable) {
                    Log.e("HAND_MODEL", closeError.message.toString())
                }
            }
        }
    }

    private fun prepareModelFile(context: Context): File? {
        val modelFile = File(context.filesDir, MODEL_FILE_NAME)

        if (modelFile.exists() && modelFile.length() >= MIN_MODEL_BYTES) {
            return modelFile
        }

        return try {
            copyAssetToFile(context, MODEL_FILE_NAME, modelFile)

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
