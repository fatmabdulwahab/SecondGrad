package com.example.secondgrad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
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
    private var routeSearchToken = 0

    fun onFromTextChange(value: String) {
        _fromText.value = value
        _uiState.value = _uiState.value.copy(fromText = value)
    }

    fun onToTextChange(value: String) {
        _toText.value = value
        _uiState.value = _uiState.value.copy(toText = value)
    }

    fun searchRoutes() {
        val userLocation = sanitizeFromLocation(_fromText.value)
        val destination = sanitizeToLocation(_toText.value)

        if (!canFillLocationField(userLocation) || !canFillLocationField(destination)) {
            _errorMessage.value = "اكتبي نقطة البداية والوجهة"
            _uiState.value = _uiState.value.copy(errorMessage = "اكتبي نقطة البداية والوجهة")
            return
        }

        onFromTextChange(userLocation)
        onToTextChange(destination)

        viewModelScope.launch {
            runRouteSearch(userLocation, destination, showErrors = true)
        }
    }

    private suspend fun runRouteSearch(
        userLocation: String,
        destination: String,
        showErrors: Boolean
    ): String? {
        val cleanFrom = sanitizeFromLocation(userLocation)
        val cleanTo = sanitizeToLocation(destination)

        if (!canFillLocationField(cleanFrom) || !canFillLocationField(cleanTo)) {
            val invalidMessage = "اكتبي نقطة البداية والوجهة بوضوح"
            if (showErrors) {
                _errorMessage.value = invalidMessage
                _uiState.value = _uiState.value.copy(errorMessage = invalidMessage)
            }
            return invalidMessage
        }

        routeSearchToken = routeSearchToken + 1
        val searchToken = routeSearchToken

        _isLoading.value = true
        if (showErrors) {
            _errorMessage.value = null
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = if (showErrors) null else _errorMessage.value)

        var resultMessage: String? = null

        try {
            val response = repository.searchRoutes(
                SearchRouteRequest(
                    userLocation = cleanFrom,
                    destination = cleanTo
                )
            )

            if (searchToken != routeSearchToken) {
                return null
            }

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
            if (searchToken != routeSearchToken) {
                return null
            }

            _routes.value = java.util.ArrayList<RouteData>()
            _uiState.value = _uiState.value.copy(routes = java.util.ArrayList<RouteData>())
            resultMessage = mapRouteSearchError(throwable)
            if (showErrors) {
                _errorMessage.value = resultMessage
                _uiState.value = _uiState.value.copy(errorMessage = resultMessage)
            }
        }

        if (searchToken == routeSearchToken) {
            _isLoading.value = false
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
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
                        val fromForSearch = sanitizeFromLocation(fieldValues.origin)
                        val toForSearch = sanitizeToLocation(fieldValues.destination)
                        val hasFrom = canFillLocationField(fromForSearch)
                        val hasTo = canFillLocationField(toForSearch)

                        if (hasFrom) {
                            onFromTextChange(fromForSearch)
                        }
                        if (hasTo) {
                            onToTextChange(toForSearch)
                        }

                        message = if (hasFrom && hasTo) {
                            val searchError = runRouteSearch(fromForSearch, toForSearch, showErrors = false)
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

        return ParsedVoiceRoute(
            sanitizeFromLocation(fromValue),
            sanitizeToLocation(toValue)
        )
    }

    private fun canFillLocationField(value: String): Boolean {
        if (!hasText(value) || isFillerText(value)) {
            return false
        }

        val normalized = normalizeVoiceText(value)
        if (normalized.length < 3) {
            return false
        }

        val blockedWords = arrayOf("من", "الى", "إلى", "to", "from")
        var blockedIndex = 0
        while (blockedIndex < blockedWords.size) {
            if (normalized == blockedWords[blockedIndex]) {
                return false
            }
            blockedIndex = blockedIndex + 1
        }

        return true
    }

    private fun sanitizeFromLocation(value: String): String {
        return sanitizeLocationText(value, isFromField = true)
    }

    private fun sanitizeToLocation(value: String): String {
        return sanitizeLocationText(value, isFromField = false)
    }

    private fun sanitizeLocationText(value: String, isFromField: Boolean): String {
        var text = trimText(value)
        if (!hasText(text) || isFillerText(text)) {
            return ""
        }

        val prefixes = if (isFromField) {
            arrayOf("من ", "من")
        } else {
            arrayOf("إلى ", "الى ", "إلى", "الى", " to ", "to ")
        }

        var prefixIndex = 0
        while (prefixIndex < prefixes.size) {
            val prefix = prefixes[prefixIndex]
            if (text.length >= prefix.length && text.substring(0, prefix.length) == prefix) {
                text = trimText(text.substring(prefix.length))
                break
            }
            prefixIndex = prefixIndex + 1
        }

        if (!hasText(text) || isFillerText(text)) {
            return ""
        }

        return text
    }

    private fun mapRouteSearchError(throwable: Throwable): String {
        if (throwable is HttpException && throwable.code() == 404) {
            return "مفيش طرق متاحة للبحث ده"
        }

        val message = throwable.localizedMessage ?: ""
        if (message.indexOf("404") >= 0) {
            return "مفيش طرق متاحة للبحث ده"
        }

        return if (message.length > 0) {
            message
        } else {
            "حصل خطأ في الاتصال"
        }
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
            val index = text.indexOf(marker)
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
                val index = text.indexOf(marker, fromStart)
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
            val index = text.indexOf(marker)
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
        return normalized.indexOf("audio load error") >= 0 ||
            normalized.indexOf("format not recognised") >= 0 ||
            normalized.indexOf("format not recognized") >= 0
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
