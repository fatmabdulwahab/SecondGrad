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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun createSession() {
        viewModelScope.launch {
            createSessionForCamera()
        }
    }

    suspend fun createSessionForCamera(): Boolean {
        return try {
            val response = repository.createSession()
            _sessionId.value = response.session_id
            _prediction.value = ""
            _currentWord.value = ""
            _errorMessage.value = null
            true
        } catch (e: Exception) {
            Log.e("SIGN_SESSION_ERROR", e.message.toString())
            _errorMessage.value = "مش قادرين نبدأ جلسة الكاميرا"
            false
        }
    }

    fun resetWord() {
        viewModelScope.launch {
            val oldSession = _sessionId.value
            _prediction.value = ""
            _currentWord.value = ""

            if (oldSession.length > 0) {
                try {
                    repository.endSession(oldSession)
                } catch (e: Exception) {
                    Log.e("SIGN_RESET_ERROR", e.message.toString())
                }
            }

            val response = repository.createSession()
            _sessionId.value = response.session_id
        }
    }

    fun deleteLastCharacter() {
        val word = _currentWord.value
        if (word.length > 0) {
            _currentWord.value = word.substring(0, word.length - 1)
        }
    }

    fun addSpace() {
        _currentWord.value = _currentWord.value + " "
    }

    suspend fun finishSession(): String {
        val finalWord = _currentWord.value
        val currentSession = _sessionId.value

        if (currentSession.length > 0) {
            repository.endSession(currentSession)
        }

        _sessionId.value = ""
        _prediction.value = ""
        _currentWord.value = ""

        return finalWord
    }

    fun setCameraError(message: String) {
        _errorMessage.value = message
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun recognize(
        landmarks: List<Float>
    ) {
        Log.d("VM", "recognize ENTERED")

        val currentSession = _sessionId.value
        Log.d("VM", "session = $currentSession")

        if (currentSession.length == 0) return

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
                    response.prediction

                val serverWord = response.current_word
                if (serverWord != null && serverWord.length > 0) {
                    _currentWord.value = serverWord
                }

            } catch (e: Exception) {
                Log.e("SIGN_RECOGNIZE_ERROR", e.message.toString())
            }
        }
    }
}
