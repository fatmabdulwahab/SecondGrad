package com.example.secondgrad

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignViewModel : ViewModel() {

    private val repository = SignRepository()

    private val _sessionId = MutableStateFlow("")
    val sessionId = _sessionId.asStateFlow()

    private val _prediction = MutableStateFlow("")
    val prediction = _prediction.asStateFlow()

    private val _currentWord = MutableStateFlow("")
    val currentWord = _currentWord.asStateFlow()

    fun createSession() {

        viewModelScope.launch {

            try {

                val response =
                    repository.createSession()

                _sessionId.value =
                    response.session_id

            } catch (e: Exception) {

            }
        }
    }

    fun recognize(
        landmarks: List<Float>
    ) {
        Log.d("VM", "recognize ENTERED")

        val currentSession = _sessionId.value
        Log.d("VM", "session = $currentSession")

        if (currentSession.isBlank()) return

        viewModelScope.launch {

            try {

                val response =
                    repository.recognize(
                        RecognizeRequest(
                            session_id = currentSession,
                            landmarks = landmarks
                        )
                    )

                _prediction.value =
                    response.prediction ?: ""

                _currentWord.value =
                    response.current_word ?: ""

            } catch (e: Exception) {

            }
        }
    }
}