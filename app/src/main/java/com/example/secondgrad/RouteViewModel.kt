package com.example.secondgrad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

data class RouteUiState(
    val fromText: String = "",
    val toText: String = "",
    val routes: List<RouteData> = java.util.ArrayList<RouteData>(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isVoiceSending: Boolean = false,
    val voiceMessage: String? = null
)

class RouteViewModel : ViewModel() {

    private val repository = RouteRepository()

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState = _uiState.asStateFlow()

    private val _fromText = MutableStateFlow("")
    val fromText = _fromText.asStateFlow()

    private val _toText = MutableStateFlow("")
    val toText = _toText.asStateFlow()

    private val _routes = MutableStateFlow<List<RouteData>>(java.util.ArrayList<RouteData>())
    val routes = _routes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _isVoiceSending = MutableStateFlow(false)
    val isVoiceSending = _isVoiceSending.asStateFlow()

    private val _voiceMessage = MutableStateFlow<String?>(null)
    val voiceMessage = _voiceMessage.asStateFlow()

    fun onFromTextChange(value: String) {
        _fromText.value = value
        _uiState.value = _uiState.value.copy(fromText = value)
    }

    fun onToTextChange(value: String) {
        _toText.value = value
        _uiState.value = _uiState.value.copy(toText = value)
    }

    fun searchRoutes() {
        val userLocation = _fromText.value
        val destination = _toText.value

        if (!hasText(userLocation) || !hasText(destination)) {
            _errorMessage.value = "اكتبي نقطة البداية والوجهة"
            _uiState.value = _uiState.value.copy(errorMessage = "اكتبي نقطة البداية والوجهة")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val response = repository.searchRoutes(
                    SearchRouteRequest(
                        userLocation = userLocation,
                        destination = destination
                    )
                )

                _routes.value = response.data
                _uiState.value = _uiState.value.copy(routes = response.data)

                if (response.data.size == 0) {
                    _errorMessage.value = "مفيش طرق متاحة للبحث ده"
                    _uiState.value = _uiState.value.copy(errorMessage = "مفيش طرق متاحة للبحث ده")
                }
            } catch (throwable: Throwable) {
                _routes.value = java.util.ArrayList<RouteData>()
                _uiState.value = _uiState.value.copy(routes = java.util.ArrayList<RouteData>())
                _errorMessage.value = throwable.localizedMessage ?: "حصل خطأ في الاتصال"
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.localizedMessage ?: "حصل خطأ في الاتصال"
                )
            }

            _isLoading.value = false
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun clearError() {
        _errorMessage.value = null
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun sendVoice(file: File) {
        viewModelScope.launch {
            _isVoiceSending.value = true
            _voiceMessage.value = null
            _errorMessage.value = null
            _uiState.value = _uiState.value.copy(
                isVoiceSending = true,
                voiceMessage = null,
                errorMessage = null
            )

            try {
                val response = repository.sendVoice(file)
                val voiceResult = parseVoiceSearchResult(response.data)
                var message = response.message
                    ?: response.status
                    ?: "تم إرسال الصوت بنجاح"

                if (voiceResult != null) {
                    if (hasText(voiceResult.origin)) {
                        _fromText.value = voiceResult.origin
                    }

                    if (hasText(voiceResult.destination)) {
                        _toText.value = voiceResult.destination
                    }

                    _uiState.value = _uiState.value.copy(
                        fromText = _fromText.value,
                        toText = _toText.value
                    )

                    message = "تم استخراج الرحلة من الصوت"
                }

                _voiceMessage.value = message
                _uiState.value = _uiState.value.copy(voiceMessage = message)
            } catch (throwable: Throwable) {
                _errorMessage.value = throwable.localizedMessage ?: "حصل خطأ في إرسال الصوت"
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.localizedMessage ?: "حصل خطأ في إرسال الصوت"
                )
            }

            _isVoiceSending.value = false
            _uiState.value = _uiState.value.copy(isVoiceSending = false)
        }
    }

    fun clearVoiceMessage() {
        _voiceMessage.value = null
        _uiState.value = _uiState.value.copy(voiceMessage = null)
    }

    private fun hasText(value: String): Boolean {
        var index = 0
        while (index < value.length) {
            val char = value[index]
            if (char != ' ' && char != '\n' && char != '\t' && char != '\r') {
                return true
            }
            index++
        }
        return false
    }

    private fun parseVoiceSearchResult(data: String?): VoiceSearchResult? {
        if (data == null || data.length == 0) {
            return null
        }

        return try {
            val jsonObject = JSONObject(data)
            val transcription = getJsonText(jsonObject, "transcription")
            val origin = getJsonText(jsonObject, "origin")
            val destination = getJsonText(jsonObject, "destination")
            val status = getJsonText(jsonObject, "status")

            if (!hasText(origin) && !hasText(destination)) {
                null
            } else {
                VoiceSearchResult(
                    transcription = transcription,
                    origin = origin,
                    destination = destination,
                    status = status
                )
            }
        } catch (throwable: Throwable) {
            null
        }
    }

    private fun getJsonText(jsonObject: JSONObject, key: String): String {
        return if (jsonObject.has(key)) {
            jsonObject.optString(key, "")
        } else {
            ""
        }
    }
}
