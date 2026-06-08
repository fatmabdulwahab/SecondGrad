package com.example.secondgrad


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
)
data class CreateSessionResponse(
    val session_id: String
)
