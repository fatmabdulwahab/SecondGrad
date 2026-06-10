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

    private var previousPrediction = ""

    fun createSession() {
        viewModelScope.launch {
            createSessionForCamera()
        }
    }

    fun fetchCameraSessionIdBlocking(): String {
        return try {
            repository.createSession().resolvedSessionId()
        } catch (e: Exception) {
            Log.e("SIGN_SESSION_ERROR", e.message.toString())
            ""
        }
    }

    fun applyCameraSession(sessionId: String): Boolean {
        if (sessionId.length == 0) {
            _errorMessage.value = "مش قادرين نبدأ جلسة الكاميرا"
            return false
        }

        resetRecognitionState()
        _sessionId.value = sessionId
        _errorMessage.value = null
        return true
    }

    fun createSessionForCameraBlocking(): Boolean {
        return applyCameraSession(fetchCameraSessionIdBlocking())
    }

    suspend fun createSessionForCamera(): Boolean {
        return createSessionForCameraBlocking()
    }

    fun resetWord() {
        viewModelScope.launch {
            val oldSession = _sessionId.value
            resetRecognitionState()

            if (oldSession.length > 0) {
                try {
                    repository.endSession(oldSession)
                } catch (e: Exception) {
                    Log.e("SIGN_RESET_ERROR", e.message.toString())
                }
            }

            val response = repository.createSession()
            _sessionId.value = response.resolvedSessionId()
        }
    }

    fun deleteLastCharacter() {
        val word = _currentWord.value
        if (word.length > 0) {
            _currentWord.value = substringText(word, 0, word.length - 1)
            previousPrediction = charAtEnd(_currentWord.value)?.toString() ?: ""
        } else {
            previousPrediction = ""
        }
    }

    fun addSpace() {
        _currentWord.value = _currentWord.value + " "
        previousPrediction = ""
    }

    suspend fun finishSession(): String {
        val finalWord = buildFinalWord()
        val currentSession = _sessionId.value

        if (currentSession.length > 0) {
            repository.endSession(currentSession)
        }

        resetRecognitionState()
        _sessionId.value = ""

        return finalWord
    }

    fun cancelSession(
        onComplete: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                finishSession()
                onComplete()
            } catch (e: Exception) {
                Log.e("CANCEL_SESSION_ERROR", e.message.toString())
                onError()
            }
        }
    }

    fun saveSession(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val finalWord = finishSession()
                if (finalWord.length > 0) {
                    onSuccess(finalWord)
                } else {
                    onError("مفيش كلمة محفوظة — جرّبي الإشارة تاني")
                }
            } catch (e: Exception) {
                Log.e("END_SESSION_ERROR", e.message.toString())
                onError("مش قادرين نحفظ الجلسة")
            }
        }
    }

    fun setCameraError(message: String) {
        _errorMessage.value = message
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun recognize(landmarks: List<Float>) {
        val currentSession = _sessionId.value
        if (currentSession.length == 0) {
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.recognize(
                    RecognizeRequest(
                        session_id = currentSession,
                        landmarks = landmarks
                    )
                )
                applyRecognizeResponse(response)
            } catch (e: Exception) {
                Log.e("SIGN_RECOGNIZE_ERROR", e.message.toString())
            }
        }
    }

    private fun applyRecognizeResponse(response: RecognizeResponse) {
        val letter = trimInputText(response.prediction)
        _prediction.value = letter

        val serverWord = response.resolvedAccumulatedWord()
        if (serverWord.length > 0) {
            _currentWord.value = serverWord
            previousPrediction = letter
            return
        }

        if (letter.length == 0 || response.confidence < 0.35) {
            return
        }

        val word = _currentWord.value
        if (word.length == 0) {
            _currentWord.value = letter
            previousPrediction = letter
            return
        }

        if (previousPrediction.length > 0 && letter != previousPrediction) {
            var updatedWord = word
            if (!endsWithText(updatedWord, previousPrediction)) {
                updatedWord = updatedWord + previousPrediction
            }
            if (!endsWithText(updatedWord, letter)) {
                updatedWord = updatedWord + letter
            }
            _currentWord.value = updatedWord
            previousPrediction = letter
        }
    }

    private fun buildFinalWord(): String {
        val accumulatedWord = _currentWord.value
        val liveLetter = trimInputText(_prediction.value)

        if (accumulatedWord.length > 0) {
            if (liveLetter.length > 0 && !endsWithText(accumulatedWord, liveLetter)) {
                return accumulatedWord + liveLetter
            }
            return accumulatedWord
        }

        return liveLetter
    }

    private fun resetRecognitionState() {
        _prediction.value = ""
        _currentWord.value = ""
        previousPrediction = ""
    }
}
