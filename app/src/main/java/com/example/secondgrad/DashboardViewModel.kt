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

    private val _roles = MutableStateFlow<List<RoleResponse>>(java.util.ArrayList<RoleResponse>())
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

    fun addRole(roleName: String) {
        val trimmedName = roleName.trim()
        if (trimmedName.length == 0) {
            _message.value = "اكتبي اسم الصلاحية"
            return
        }

        viewModelScope.launch {
            runCatching {
                repository.addRole(trimmedName)
            }.onSuccess { createdRole ->
                val updatedRoles = java.util.ArrayList(_roles.value)
                updatedRoles.add(createdRole)
                _roles.value = updatedRoles
                _message.value = "تمت إضافة الصلاحية"
            }.onFailure {
                _message.value = "مش قادرين نضيف الصلاحية"
            }
        }
    }

    fun editRole(role: RoleResponse, newName: String) {
        val roleId = role.id
        val trimmedName = newName.trim()

        if (roleId == null) {
            _message.value = "الصلاحية مش معروفة"
            return
        }

        if (trimmedName.length == 0) {
            _message.value = "اكتبي اسم الصلاحية"
            return
        }

        viewModelScope.launch {
            runCatching {
                repository.editRole(roleId, trimmedName)
            }.onSuccess { updatedRole ->
                val updatedRoles = java.util.ArrayList<RoleResponse>()
                for (item in _roles.value) {
                    if (item.id == roleId) {
                        updatedRoles.add(updatedRole)
                    } else {
                        updatedRoles.add(item)
                    }
                }
                _roles.value = updatedRoles
                _message.value = "تم تعديل الصلاحية"
            }.onFailure {
                _message.value = "مش قادرين نعدل الصلاحية"
            }
        }
    }

    fun deleteRole(role: RoleResponse) {
        val roleId = role.id ?: return

        viewModelScope.launch {
            runCatching {
                repository.deleteRole(roleId)
            }.onSuccess {
                val updatedRoles = java.util.ArrayList<RoleResponse>()
                for (item in _roles.value) {
                    if (item.id != roleId) {
                        updatedRoles.add(item)
                    }
                }
                _roles.value = updatedRoles
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
