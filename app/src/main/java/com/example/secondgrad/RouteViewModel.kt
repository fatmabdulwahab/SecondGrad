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

    private val _closeVoicePanel = MutableStateFlow(false)
    val closeVoicePanel = _closeVoicePanel.asStateFlow()

    private var isVoiceSendInProgress = false

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
            runRouteSearch(userLocation, destination, showErrors = true)
        }
    }

    private suspend fun runRouteSearch(
        userLocation: String,
        destination: String,
        showErrors: Boolean
    ): String? {
        _isLoading.value = true
        if (showErrors) {
            _errorMessage.value = null
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = if (showErrors) null else _errorMessage.value)

        var resultMessage: String? = null

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
                resultMessage = "مفيش طرق متاحة للبحث ده"
                if (showErrors) {
                    _errorMessage.value = resultMessage
                    _uiState.value = _uiState.value.copy(errorMessage = resultMessage)
                }
            }
        } catch (throwable: Throwable) {
            _routes.value = java.util.ArrayList<RouteData>()
            _uiState.value = _uiState.value.copy(routes = java.util.ArrayList<RouteData>())
            resultMessage = throwable.localizedMessage ?: "حصل خطأ في الاتصال"
            if (showErrors) {
                _errorMessage.value = resultMessage
                _uiState.value = _uiState.value.copy(errorMessage = resultMessage)
            }
        }

        _isLoading.value = false
        _uiState.value = _uiState.value.copy(isLoading = false)
        return resultMessage
    }

    fun clearError() {
        _errorMessage.value = null
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun sendVoice(file: File) {
        if (isVoiceSendInProgress) {
            return
        }

        viewModelScope.launch {
            isVoiceSendInProgress = true
            _isVoiceSending.value = true
            _voiceMessage.value = null
            _closeVoicePanel.value = false
            _uiState.value = _uiState.value.copy(
                isVoiceSending = true,
                voiceMessage = null
            )

            var message = "حصل خطأ في إرسال الصوت"

            try {
                val response = repository.sendVoice(file)
                val voiceResult = parseVoiceSearchResult(response.dataPayload)
                message = response.message
                    ?: response.status
                    ?: "تم إرسال الصوت بنجاح"

                if (voiceResult != null) {
                    if (hasText(voiceResult.errorDetail)) {
                        message = if (containsAudioLoadError(voiceResult.errorDetail)) {
                            "السيرفر مش قادر يقرأ ملف الصوت — استني ثانية بعد الإيقاف وجربي تاني"
                        } else {
                            "مش قادرين نفهم التسجيل: ${voiceResult.errorDetail}"
                        }
                    } else {
                        val fieldValues = buildVoiceFieldValues(voiceResult)
                        applyVoiceFieldsToUi(fieldValues.origin, fieldValues.destination)

                        val hasFrom = hasFilledLocationField(_fromText.value)
                        val hasTo = hasFilledLocationField(_toText.value)
                        message = if (hasFrom && hasTo) {
                            val searchError = runRouteSearch(_fromText.value, _toText.value, showErrors = false)
                            _closeVoicePanel.value = true
                            if (searchError == null) {
                                "تم استخراج الرحلة والبحث عن الطرق"
                            } else {
                                "تم استخراج الرحلة، لكن $searchError"
                            }
                        } else if (hasFrom || hasTo) {
                            "تم تعبئة الحقول من الصوت — راجعي From و To"
                        } else if (hasText(voiceResult.transcription)) {
                            "السيرفر سمع: ${voiceResult.transcription} — جرّبي بوضوح: من المعادي إلى رمسيس (3 ثواني)"
                        } else {
                            "مش قادرين نفهم التسجيل، جرّبي تاني بوضوح"
                        }
                    }
                } else {
                    message = if (response.success == true) {
                        "السيرفر استلم الصوت لكن مفيش نص واضح — قولي: من المعادي إلى رمسيس (3 ثواني)"
                    } else {
                        "مفيش بيانات واضحة من الصوت، جرّبي تاني"
                    }
                }
            } catch (throwable: Throwable) {
                message = throwable.localizedMessage ?: "حصل خطأ في إرسال الصوت"
            } finally {
                if (file.exists()) {
                    file.delete()
                }

                _voiceMessage.value = message
                _uiState.value = _uiState.value.copy(voiceMessage = message)
                _isVoiceSending.value = false
                _uiState.value = _uiState.value.copy(isVoiceSending = false)
                isVoiceSendInProgress = false
            }
        }
    }

    fun clearVoiceMessage() {
        _voiceMessage.value = null
        _closeVoicePanel.value = false
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

        var payload = trimText(data)
        var voiceResult = parseVoiceJsonObject(payload)
        if (voiceResult != null) {
            return voiceResult
        }

        if (payload.length > 1 && payload[0] == '"' && payload[payload.length - 1] == '"') {
            payload = unwrapJsonString(payload)
            voiceResult = parseVoiceJsonObject(payload)
            if (voiceResult != null) {
                return voiceResult
            }
        }

        return null
    }

    private fun parseVoiceJsonObject(payload: String): VoiceSearchResult? {
        return try {
            val jsonObject = JSONObject(payload)
            val transcription = getJsonText(
                jsonObject,
                "transcription",
                "Transcription",
                "text",
                "Text",
                "recognizedText",
                "RecognizedText"
            )
            val origin = getJsonText(
                jsonObject,
                "origin",
                "Origin",
                "userLocation",
                "UserLocation",
                "from",
                "From",
                "startPoint",
                "StartPoint",
                "start",
                "Start"
            )
            val destination = getJsonText(
                jsonObject,
                "destination",
                "Destination",
                "to",
                "To",
                "endPoint",
                "EndPoint",
                "end",
                "End"
            )
            val status = getJsonText(jsonObject, "status", "Status")
            val errorDetail = getJsonText(
                jsonObject,
                "detail",
                "Detail",
                "error",
                "Error",
                "errorMessage",
                "ErrorMessage"
            )

            if (hasText(errorDetail) && !hasText(transcription) && !hasText(origin) && !hasText(destination)) {
                VoiceSearchResult(
                    transcription = "",
                    origin = "",
                    destination = "",
                    status = status,
                    errorDetail = errorDetail
                )
            } else if (!hasText(origin) && !hasText(destination) && !hasText(transcription)) {
                null
            } else {
                VoiceSearchResult(
                    transcription = transcription,
                    origin = origin,
                    destination = destination,
                    status = status,
                    errorDetail = errorDetail
                )
            }
        } catch (throwable: Throwable) {
            null
        }
    }

    private fun unwrapJsonString(value: String): String {
        return try {
            JSONObject("{\"value\":$value}").getString("value")
        } catch (throwable: Throwable) {
            if (value.length > 2) {
                value.substring(1, value.length - 1)
            } else {
                value
            }
        }
    }

    private fun buildVoiceFieldValues(voiceResult: VoiceSearchResult): ParsedVoiceRoute {
        var fromValue = voiceResult.origin
        var toValue = voiceResult.destination
        val transcription = voiceResult.transcription

        if (hasText(transcription) && !isFillerText(transcription)) {
            val parsedRoute = parseRouteFromTranscription(transcription)
            if (!hasText(fromValue) && hasText(parsedRoute.origin)) {
                fromValue = parsedRoute.origin
            }
            if (!hasText(toValue) && hasText(parsedRoute.destination)) {
                toValue = parsedRoute.destination
            }
            if (!hasText(fromValue) && !hasText(toValue)) {
                fromValue = transcription
            }
        }

        return ParsedVoiceRoute(fromValue, toValue)
    }

    private fun applyVoiceFieldsToUi(fromValue: String, toValue: String) {
        if (canFillLocationField(fromValue)) {
            onFromTextChange(fromValue)
        }
        if (canFillLocationField(toValue)) {
            onToTextChange(toValue)
        }
    }

    private fun hasFilledLocationField(value: String): Boolean {
        return canFillLocationField(value)
    }

    private fun canFillLocationField(value: String): Boolean {
        if (!hasText(value) || isFillerText(value)) {
            return false
        }
        return normalizeVoiceText(value).length >= 2
    }

    private fun getJsonText(jsonObject: JSONObject, vararg keys: String): String {
        var keyIndex = 0
        while (keyIndex < keys.size) {
            val key = keys[keyIndex]
            if (jsonObject.has(key) && !jsonObject.isNull(key)) {
                val value = jsonObject.optString(key, "")
                if (value.length > 0 && value != "null") {
                    return value
                }
            }
            keyIndex = keyIndex + 1
        }
        return ""
    }

    private data class ParsedVoiceRoute(
        val origin: String,
        val destination: String
    )

    private fun parseRouteFromTranscription(transcription: String): ParsedVoiceRoute {
        val text = trimText(transcription)
        val fromMarkers = arrayOf("من ", "من")
        val toMarkers = arrayOf(
            " إلى ",
            " الى ",
            " إلى",
            "إلى",
            " الى",
            "الى",
            " to ",
            " to"
        )

        var fromIndex = -1
        var fromMarkerLength = 0
        var fromMarkerIndex = 0
        while (fromMarkerIndex < fromMarkers.size) {
            val marker = fromMarkers[fromMarkerIndex]
            val index = indexOfText(text, marker)
            if (index >= 0 && (fromIndex < 0 || index < fromIndex)) {
                fromIndex = index
                fromMarkerLength = marker.length
            }
            fromMarkerIndex = fromMarkerIndex + 1
        }

        if (fromIndex >= 0) {
            val fromStart = fromIndex + fromMarkerLength
            var toIndex = -1
            var toMarkerLength = 0
            var markerIndex = 0

            while (markerIndex < toMarkers.size) {
                val marker = toMarkers[markerIndex]
                val index = indexOfTextFrom(text, marker, fromStart)
                if (index >= 0 && (toIndex < 0 || index < toIndex)) {
                    toIndex = index
                    toMarkerLength = marker.length
                }
                markerIndex = markerIndex + 1
            }

            if (toIndex < 0) {
                return ParsedVoiceRoute(trimText(text.substring(fromStart)), "")
            }

            val origin = trimText(text.substring(fromStart, toIndex))
            val destination = trimText(text.substring(toIndex + toMarkerLength))
            return ParsedVoiceRoute(origin, destination)
        }

        var splitIndex = -1
        var splitLength = 0
        var markerIndex = 0
        while (markerIndex < toMarkers.size) {
            val marker = toMarkers[markerIndex]
            val index = indexOfText(text, marker)
            if (index > 0 && (splitIndex < 0 || index < splitIndex)) {
                splitIndex = index
                splitLength = marker.length
            }
            markerIndex = markerIndex + 1
        }

        if (splitIndex > 0) {
            val origin = trimText(text.substring(0, splitIndex))
            val destination = trimText(text.substring(splitIndex + splitLength))
            return ParsedVoiceRoute(origin, destination)
        }

        return ParsedVoiceRoute("", "")
    }

    private fun trimText(value: String): String {
        var start = 0
        var end = value.length

        while (start < end) {
            val char = value[start]
            if (char != ' ' && char != '\n' && char != '\t' && char != '\r') {
                break
            }
            start = start + 1
        }

        while (end > start) {
            val char = value[end - 1]
            if (char != ' ' && char != '\n' && char != '\t' && char != '\r') {
                break
            }
            end = end - 1
        }

        return if (start >= end) "" else value.substring(start, end)
    }

    private fun isFillerText(value: String): Boolean {
        if (!hasText(value)) {
            return true
        }

        val normalized = normalizeVoiceText(value)
        val fillers = arrayOf("ايه", "إيه", "اي", "إي", "اه", "آه", "مم", "mm", "eh", "what")
        var fillerIndex = 0
        while (fillerIndex < fillers.size) {
            if (normalized == fillers[fillerIndex]) {
                return true
            }
            fillerIndex = fillerIndex + 1
        }

        return false
    }

    private fun normalizeVoiceText(value: String): String {
        val trimmed = trimText(value)
        val builder = StringBuilder()
        var index = 0
        while (index < trimmed.length) {
            val char = trimmed[index]
            if (isIgnorableVoiceChar(char)) {
                index = index + 1
                continue
            }
            if (char >= 'A' && char <= 'Z') {
                builder.append((char.code + 32).toChar())
            } else {
                builder.append(char)
            }
            index = index + 1
        }
        return builder.toString()
    }

    private fun containsAudioLoadError(value: String): Boolean {
        val normalized = normalizeVoiceText(value)
        return indexOfText(normalized, "audio load error") >= 0 ||
            indexOfText(normalized, "format not recognised") >= 0 ||
            indexOfText(normalized, "format not recognized") >= 0
    }

    private fun isIgnorableVoiceChar(char: Char): Boolean {
        if (char == '؟' || char == '،' || char == '.' || char == '!' || char == '?' || char == ',') {
            return true
        }
        if (char == '\u0640') {
            return true
        }
        val code = char.code
        return code in 0x064B..0x065F || code == 0x0670
    }
}
