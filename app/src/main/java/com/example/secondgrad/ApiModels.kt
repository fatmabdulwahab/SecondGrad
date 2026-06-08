package com.example.secondgrad

import com.google.gson.annotations.SerializedName

data class ApiPage<T>(
    @SerializedName("pageIndex") val pageIndex: Int? = null,
    @SerializedName("pageSize") val pageSize: Int? = null,
    @SerializedName("count") val count: Int? = null,
    @SerializedName("data") val data: List<T> = java.util.ArrayList<T>()
)

data class ApiMessageResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("success") val success: Boolean? = null
)

data class SignUpRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val country: String,
    val address: String,
    val currentLatitude: Double? = null,
    val currentLongitude: Double? = null
)

data class SignInRequest(
    val email: String,
    val password: String
)

data class GoogleLoginRequest(
    val idToken: String
)

data class UpdateUserDataRequest(
    val fullName: String,
    val country: String,
    val address: String
)

data class UpdatePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class EmailRequest(
    val email: String
)

data class VerifyResetCodeRequest(
    val email: String,
    val code: String
)

data class AuthResponse(
    val token: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val message: String? = null
)

data class FeedbackRequest(
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val reason: String,
    val timeSlot: String,
    val message: String,
    val comment: String,
    val ratingId: Int,
    val userProfileId: Int,
    val routeId: Int,
    val tripStatusId: Int
)

data class FeedbackResponse(
    val id: Int? = null,
    val fullName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val reason: String? = null,
    val timeSlot: String? = null,
    val message: String? = null,
    val comment: String? = null,
    val ratingId: Int? = null,
    val userProfileId: Int? = null,
    val routeId: Int? = null,
    val tripStatusId: Int? = null
)

data class NotificationRequest(
    val userId: Int,
    val message: String,
    val notificationType: String
)

data class NotificationResponse(
    val id: Int? = null,
    val userId: Int? = null,
    val message: String? = null,
    val notificationType: String? = null,
    val isRead: Boolean? = null,
    val createdAt: String? = null
)

data class RoleNameRequest(
    val roleName: String
)

data class EditRoleRequest(
    val id: Int,
    val name: String
)

data class UpdateUserRolesRequest(
    val userId: Int,
    val roles: List<String>
)

data class RoleResponse(
    val id: Int? = null,
    val name: String? = null
)

data class DashboardRouteRequest(
    val name: String,
    val startPoint: String,
    val endPoint: String,
    val region: String,
    val description: String,
    val ticketPrice: Int,
    val averageTimeInMinutes: Int,
    val routeStatusId: Int
)

data class UpdateDashboardRouteRequest(
    val id: Int,
    val name: String,
    val startPoint: String,
    val endPoint: String,
    val region: String,
    val description: String,
    val ticketPrice: Int,
    val averageTimeInMinutes: Int,
    val routeStatusId: Int
)

data class StationRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

data class UpdateStationRequest(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double
)

data class StationResponse(
    val id: Int? = null,
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class HistoryTripResponse(
    val id: Int? = null,
    val userLocation: String? = null,
    val destination: String? = null,
    val routeName: String? = null,
    val createdAt: String? = null
)

data class CountResponse(
    val count: Int? = null
)
