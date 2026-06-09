package com.example.secondgrad.screens.scoffold

import android.util.Log

object MediaPipeNativeSupport {

    private var checked = false
    private var available = false

    fun isAvailable(): Boolean {
        if (checked) {
            return available
        }
        checked = true

        val libraryNames = arrayOf(
            "mediapipe_tasks_vision_jni",
            "mediapipe_tasks_jni"
        )

        var libraryIndex = 0
        while (libraryIndex < libraryNames.size) {
            val libraryName = libraryNames[libraryIndex]
            try {
                System.loadLibrary(libraryName)
                available = true
                return true
            } catch (linkError: UnsatisfiedLinkError) {
                Log.w("MEDIAPIPE_NATIVE", "${libraryName}: ${linkError.message}")
            }
            libraryIndex = libraryIndex + 1
        }

        available = false
        return false
    }

    fun unavailableMessage(): String {
        return "مكتبة التعرف على الإشارة مش متوفرة على الجهاز ده. جرّبي جهاز حقيقي أو محاكي بنظام arm64 بعد تحديث التطبيق."
    }
}
