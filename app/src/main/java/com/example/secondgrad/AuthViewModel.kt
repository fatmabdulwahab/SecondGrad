package com.example.secondgrad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage = _authMessage.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess = _registerSuccess.asStateFlow()

    private val _resetCodeSent = MutableStateFlow(false)
    val resetCodeSent = _resetCodeSent.asStateFlow()

    private val _resetCodeConfirmed = MutableStateFlow(false)
    val resetCodeConfirmed = _resetCodeConfirmed.asStateFlow()

    private val _resetCompleted = MutableStateFlow(false)
    val resetCompleted = _resetCompleted.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authMessage.value = null

            try {
                val response = repository.signIn(email, password)
                val token = response.token
                if (token != null && token.length > 0) {
                    AuthSession.saveAuth(response)
                    _loginSuccess.value = true
                    _authMessage.value = "تم تسجيل الدخول بنجاح"
                } else {
                    _authMessage.value = response.message ?: "بيانات الدخول غير صحيحة"
                }
            } catch (throwable: Throwable) {
                _authMessage.value = mapNetworkErrorMessage(
                    throwable,
                    "حصل خطأ في تسجيل الدخول"
                )
            }

            _isLoading.value = false
        }
    }

    fun signUp(
        email: String,
        password: String,
        fullName: String,
        country: String,
        address: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _authMessage.value = null

            try {
                val response = repository.signUp(
                    email = email,
                    password = password,
                    fullName = fullName,
                    country = country,
                    address = address
                )

                val message = response.message ?: ""
                if (messageContainsSuccess(message) || (response.token != null && response.token.length > 0)) {
                    if (response.token != null && response.token.length > 0) {
                        AuthSession.saveAuth(response)
                    }
                    _registerSuccess.value = true
                    _authMessage.value = if (message.length > 0) message else "تم إنشاء الحساب بنجاح"
                } else {
                    _authMessage.value = if (message.length > 0) message else "مش قادرين نسجل الحساب"
                }
            } catch (throwable: Throwable) {
                _authMessage.value = mapNetworkErrorMessage(
                    throwable,
                    "حصل خطأ في إنشاء الحساب"
                )
            }

            _isLoading.value = false
        }
    }

    fun sendResetCode(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authMessage.value = null

            try {
                val response = repository.sendResetCode(email)
                if (isResetSuccessResponse(response)) {
                    _resetCodeSent.value = true
                    _authMessage.value = "تم إرسال كود التأكيد على الإيميل"
                } else {
                    _authMessage.value = mapResetErrorMessage(response)
                }
            } catch (throwable: Throwable) {
                _authMessage.value = throwable.localizedMessage ?: "حصل خطأ في إرسال الكود"
            }

            _isLoading.value = false
        }
    }

    fun confirmResetCode(email: String, code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authMessage.value = null

            try {
                val response = repository.confirmResetCode(email, code)
                if (isResetSuccessResponse(response)) {
                    _resetCodeConfirmed.value = true
                    _authMessage.value = "تم تأكيد الكود، اكتبي كلمة المرور الجديدة"
                } else {
                    _authMessage.value = mapResetErrorMessage(response)
                }
            } catch (throwable: Throwable) {
                _authMessage.value = throwable.localizedMessage ?: "حصل خطأ في تأكيد الكود"
            }

            _isLoading.value = false
        }
    }

    fun resetPassword(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authMessage.value = null

            try {
                val response = repository.resetPassword(email, password, confirmPassword)
                if (isResetSuccessResponse(response)) {
                    _resetCompleted.value = true
                    _authMessage.value = "تم تغيير كلمة المرور بنجاح"
                } else {
                    _authMessage.value = mapResetErrorMessage(response)
                }
            } catch (throwable: Throwable) {
                _authMessage.value = throwable.localizedMessage ?: "حصل خطأ في تغيير كلمة المرور"
            }

            _isLoading.value = false
        }
    }

    fun clearMessage() {
        _authMessage.value = null
    }

    fun clearLoginSuccess() {
        _loginSuccess.value = false
    }

    fun clearRegisterSuccess() {
        _registerSuccess.value = false
    }

    fun clearResetState() {
        _resetCodeSent.value = false
        _resetCodeConfirmed.value = false
        _resetCompleted.value = false
    }

    fun clearResetCompleted() {
        _resetCompleted.value = false
    }

    private fun isResetSuccessResponse(response: String): Boolean {
        val normalized = toLowerCaseAscii(trimInputText(response))
        return normalized == "success"
            || containsText(normalized, "successfully")
            || containsText(normalized, "success")
    }

    private fun mapResetErrorMessage(response: String): String {
        val normalized = trimInputText(response)
        if (normalized == "UserNotFound") {
            return "الإيميل مش موجود"
        }
        if (normalized == "InvalidCode") {
            return "الكود غلط، جرّبي تاني"
        }
        if (normalized.length > 0) {
            return normalized
        }
        return "حصل خطأ، جرّبي تاني"
    }

    private fun messageContainsSuccess(message: String): Boolean {
        val lowerMessage = toLowerCaseAscii(message)
        return containsText(lowerMessage, "registered")
            || containsText(lowerMessage, "success")
            || containsText(lowerMessage, "تم")
    }
}
