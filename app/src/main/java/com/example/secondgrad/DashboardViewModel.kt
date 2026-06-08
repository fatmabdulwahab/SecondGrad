package com.example.secondgrad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val repository = DashboardRepository()

    private val _stats = MutableStateFlow(DashboardStats())
    val stats = _stats.asStateFlow()

    private val _roles = MutableStateFlow<List<RoleResponse>>(emptyList())
    val roles = _roles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true

            runCatching {
                repository.getDashboardStats()
            }.onSuccess { loadedStats ->
                _stats.value = loadedStats
            }.onFailure {
                _message.value = "مش قادرين نحدث الإحصائيات"
            }

            runCatching {
                repository.getRoles()
            }.onSuccess { loadedRoles ->
                _roles.value = loadedRoles
            }.onFailure {
                _message.value = "مش قادرين نجيب قائمة الصلاحيات"
            }

            _isLoading.value = false
        }
    }

    fun deleteRole(role: RoleResponse) {
        val roleId = role.id ?: return

        viewModelScope.launch {
            runCatching {
                repository.deleteRole(roleId)
            }.onSuccess {
                _roles.value = _roles.value.filterNot { it.id == roleId }
                _message.value = "تم حذف الصلاحية"
            }.onFailure {
                _message.value = "مش قادرين نحذف الصلاحية"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
