package com.example.secondgrad

import com.google.gson.annotations.SerializedName

data class RecognizeRequest(
    val session_id: String?,
    val landmarks: List<Float>
)

data class RecognizeResponse(
    val session_id: String,
    val prediction: String,
    val label: String,
    val confidence: Double,
    val status: String,
    val current_word: String?,
    val nlp_result: String?
) {
    fun resolvedAccumulatedWord(): String {
        if (current_word != null && current_word.length > 0 && current_word != "null") {
            return current_word
        }
        if (nlp_result != null && nlp_result.length > 0 && nlp_result != "null") {
            return nlp_result
        }
        return ""
    }
}

data class EndSessionResult(
    val success: Boolean = false,
    val message: String = "",
    val sessionId: String = ""
)

data class CreateSessionResponse(
    @SerializedName("sessionId") val sessionId: String = "",
    @SerializedName("session_id") val session_id: String = "",
    @SerializedName("success") val success: Boolean? = null
) {
    fun resolvedSessionId(): String {
        if (sessionId.length > 0 && sessionId != "null") {
            return sessionId
        }
        if (session_id.length > 0 && session_id != "null") {
            return session_id
        }
        return ""
    }
}
