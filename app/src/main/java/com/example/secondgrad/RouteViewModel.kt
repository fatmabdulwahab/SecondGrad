package com.example.secondgrad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouteViewModel : ViewModel() {

    private val repository = RouteRepository()

    private val _fromText = MutableStateFlow("")
    val fromText = _fromText.asStateFlow()

    private val _toText = MutableStateFlow("")
    val toText = _toText.asStateFlow()

    private val _routes = MutableStateFlow<List<RouteData>>(emptyList())
    val routes = _routes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun onFromTextChange(value: String) {
        _fromText.value = value
    }

    fun onToTextChange(value: String) {
        _toText.value = value
    }

    fun searchRoutes() {
        val userLocation = _fromText.value.trim()
        val destination = _toText.value.trim()

        if (userLocation.isBlank() || destination.isBlank()) {
            _errorMessage.value = "اكتبي نقطة البداية والوجهة"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            runCatching {
                repository.searchRoutes(
                    SearchRouteRequest(
                        userLocation = userLocation,
                        destination = destination
                    )
                )
            }.onSuccess { response ->
                _routes.value = response.data
                if (response.data.isEmpty()) {
                    _errorMessage.value = "مفيش طرق متاحة للبحث ده"
                }
            }.onFailure { throwable ->
                _routes.value = emptyList()
                _errorMessage.value = throwable.localizedMessage ?: "حصل خطأ في الاتصال"
            }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
