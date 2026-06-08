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

            possibleKeys.forEach { key ->
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
            else -> return emptyList()
        }

        return rolesArray.mapIndexedNotNull { index, element ->
            if (!element.isJsonObject) return@mapIndexedNotNull null

            val obj = element.asJsonObject
            RoleResponse(
                id = obj.get("id")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }?.asInt
                    ?: index + 1,
                name = obj.get("name")?.takeIf { it.isJsonPrimitive }?.asString
                    ?: obj.get("roleName")?.takeIf { it.isJsonPrimitive }?.asString
                    ?: obj.get("normalizedName")?.takeIf { it.isJsonPrimitive }?.asString
            )
        }
    }
}

data class DashboardStats(
    val usersCount: Int = 0,
    val tripsCount: Int = 0,
    val feedbacksCount: Int = 0
)
