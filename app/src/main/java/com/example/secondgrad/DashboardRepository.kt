package com.example.secondgrad

import com.google.gson.JsonElement

class DashboardRepository {

    private val api = TransGuideRetrofit.api

    suspend fun getDashboardStats(): DashboardStats {
        val usersCount = runCatching { api.getUsersCount().asCount() }.getOrDefault(0)
        val tripsCount = runCatching { api.getTripsCount().asCount() }.getOrDefault(0)
        val feedbacksCount = runCatching { api.getFeedbacksCount().asCount() }.getOrDefault(0)

        return DashboardStats(
            usersCount = usersCount,
            tripsCount = tripsCount,
            feedbacksCount = feedbacksCount
        )
    }

    suspend fun getRoles(): List<RoleResponse> {
        return api.getAllRoles().asRoles()
    }

    suspend fun addRole(roleName: String): RoleResponse {
        return api.addRole(request = RoleNameRequest(roleName = roleName))
    }

    suspend fun editRole(id: Int, name: String): RoleResponse {
        return api.editRole(request = EditRoleRequest(id = id, name = name))
    }

    suspend fun deleteRole(id: Int) {
        api.deleteRole(id = id)
    }

    private fun JsonElement.asCount(): Int {
        if (isJsonPrimitive && asJsonPrimitive.isNumber) {
            return asInt
        }

        if (isJsonObject) {
            val obj = asJsonObject
            val possibleKeys = listOf("count", "data", "value", "total", "usersCount", "tripsCount", "feedbacksCount")

            for (key in possibleKeys) {
                val value = obj.get(key)
                if (value != null && value.isJsonPrimitive && value.asJsonPrimitive.isNumber) {
                    return value.asInt
                }
            }
        }

        return 0
    }

    private fun JsonElement.asRoles(): List<RoleResponse> {
        val rolesArray = when {
            isJsonArray -> asJsonArray
            isJsonObject && asJsonObject.get("data")?.isJsonArray == true -> asJsonObject.getAsJsonArray("data")
            else -> return java.util.ArrayList<RoleResponse>()
        }

        val roles = java.util.ArrayList<RoleResponse>()
        var index = 0
        for (element in rolesArray) {
            if (!element.isJsonObject) {
                index = index + 1
                continue
            }

            val obj = element.asJsonObject
            val roleId = obj.get("id")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }?.asInt
                ?: (index + 1)
            val roleName = obj.get("name")?.takeIf { it.isJsonPrimitive }?.asString
                ?: obj.get("roleName")?.takeIf { it.isJsonPrimitive }?.asString
                ?: obj.get("normalizedName")?.takeIf { it.isJsonPrimitive }?.asString

            roles.add(RoleResponse(id = roleId, name = roleName))
            index = index + 1
        }

        return roles
    }
}

data class DashboardStats(
    val usersCount: Int = 0,
    val tripsCount: Int = 0,
    val feedbacksCount: Int = 0
)
