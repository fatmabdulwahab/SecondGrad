package com.example.secondgrad

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface TransGuideApi {

    @GET("api/Location/GetAllRoutes")
    suspend fun getAllRoutes(
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize") pageSize: Int = 25
    ): ApiPage<RouteDetail>

    @POST("api/Location/SearchRoutes")
    suspend fun searchRoutes(
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Body request: SearchRouteRequest
    ): RouteResponse

    @GET("api/History/MyHistory")
    suspend fun getMyHistory(
        @Header("Authorization") authorization: String? = null
    ): ApiPage<HistoryTripResponse>

    @DELETE("api/History/DeleteHisrory3")
    suspend fun deleteHistory(
        @Header("Authorization") authorization: String? = null
    ): Response<Unit>

    @DELETE("api/History/DeleteTrip")
    suspend fun deleteTrip(): Response<Unit>

    @GET("api/History/GetTripsCount")
    suspend fun getTripsCount(): CountResponse

    @POST("api/UserFeedback")
    suspend fun createFeedback(
        @Body request: FeedbackRequest
    ): FeedbackResponse

    @GET("api/UserFeedbacks/GetFeedbacksCount")
    suspend fun getFeedbacksCount(): CountResponse

    @GET("api/UserFeedbacks/GetAllFeedbacks")
    suspend fun getAllFeedbacks(): ApiPage<FeedbackResponse>

    @POST("api/Auth/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): AuthResponse

    @POST("api/Auth/signin")
    suspend fun signIn(
        @Body request: SignInRequest
    ): AuthResponse

    @POST("api/Auth/google-login")
    suspend fun loginWithGoogle(
        @Body request: GoogleLoginRequest
    ): AuthResponse

    @POST("api/Auth/update-logged-user-data")
    suspend fun updateLoggedUserData(
        @Header("Authorization") authorization: String? = null,
        @Body request: UpdateUserDataRequest
    ): ApiMessageResponse

    @POST("api/Auth/update-logged-user-password")
    suspend fun updateLoggedUserPassword(
        @Header("Authorization") authorization: String? = null,
        @Body request: UpdatePasswordRequest
    ): ApiMessageResponse

    @POST("api/Auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: EmailRequest
    ): ApiMessageResponse

    @POST("api/Auth/verify-reset-code")
    suspend fun verifyResetCode(
        @Body request: VerifyResetCodeRequest
    ): ApiMessageResponse

    @GET("api/Auth/UsersCount")
    suspend fun getUsersCount(): CountResponse

    @GET("api/Notifications")
    suspend fun getUserNotifications(
        @Query("userId") userId: Int
    ): ApiPage<NotificationResponse>

    @POST("api/Notifications")
    suspend fun createNotification(
        @Body request: NotificationRequest
    ): NotificationResponse

    @GET("api/Notifications/unread")
    suspend fun getUnreadNotifications(): ApiPage<NotificationResponse>

    @GET("api/Notifications/unread/count")
    suspend fun getUnreadNotificationCount(): CountResponse

    @PATCH("api/Notifications/read-all")
    suspend fun markAllNotificationsAsRead(
        @Query("userId") userId: Int
    ): Response<Unit>

    @PATCH("api/Notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Path("id") id: Int
    ): Response<Unit>

    @DELETE("api/Notifications/{id}")
    suspend fun deleteNotification(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("api/Authorization/AddRole")
    suspend fun addRole(
        @Header("Authorization") authorization: String? = null,
        @Body request: RoleNameRequest
    ): RoleResponse

    @PUT("api/Authorization/EditRole")
    suspend fun editRole(
        @Header("Authorization") authorization: String? = null,
        @Body request: EditRoleRequest
    ): RoleResponse

    @DELETE("api/Authorization/DeleteRole")
    suspend fun deleteRole(
        @Header("Authorization") authorization: String? = null,
        @Query("id") id: Int
    ): Response<Unit>

    @GET("api/Authorization/GetAllRoles")
    suspend fun getAllRoles(
        @Header("Authorization") authorization: String? = null
    ): List<RoleResponse>

    @GET("api/Authorization/GetRole")
    suspend fun getRole(
        @Header("Authorization") authorization: String? = null,
        @Query("id") id: Int
    ): RoleResponse

    @PUT("api/Authorization/UpdateUserRoles")
    suspend fun updateUserRoles(
        @Header("Authorization") authorization: String? = null,
        @Body request: UpdateUserRolesRequest
    ): ApiMessageResponse

    @Multipart
    @POST("api/Voice/SendVoice")
    suspend fun sendVoice(
        @Part file: MultipartBody.Part
    ): ApiMessageResponse

    @Multipart
    @POST("api/Sign/frame")
    suspend fun sendSignFrame(
        @Part file: MultipartBody.Part,
        @Part("SessionId") sessionId: RequestBody,
        @Part("Type") type: RequestBody
    ): ApiMessageResponse

    @POST("api/Sign/create")
    suspend fun createTransGuideSignSession(): CreateSessionResponse

    @POST("api/Sign/end/{sessionId}")
    suspend fun endTransGuideSignSession(
        @Path("sessionId") sessionId: String
    ): Response<Unit>

    @GET("api/SignResult/{sessionId}")
    suspend fun getSignResult(
        @Path("sessionId") sessionId: String
    ): RecognizeResponse

    @POST("api/AdminDashboard/CreateRoute")
    suspend fun createRoute(
        @Body request: DashboardRouteRequest
    ): RouteDetail

    @POST("api/AdminDashboard/UpdateRoute")
    suspend fun updateRoute(
        @Body request: UpdateDashboardRouteRequest
    ): RouteDetail

    @DELETE("api/AdminDashboard/DeleteRoute")
    suspend fun deleteRoute(
        @Query("id") id: Int
    ): Response<Unit>

    @POST("api/AdminDashboard/CreateStation")
    suspend fun createStation(
        @Body request: StationRequest
    ): StationResponse

    @PATCH("api/AdminDashboard/UpdateRouteStatus")
    suspend fun updateRouteStatus(
        @Query("id") id: Int,
        @Query("status") status: String
    ): ApiMessageResponse

    @POST("api/AdminDashboard/UpdateStation")
    suspend fun updateStation(
        @Body request: UpdateStationRequest
    ): StationResponse

    @DELETE("api/AdminDashboard/DeleteStation")
    suspend fun deleteStation(
        @Query("id") id: Int
    ): Response<Unit>

    @GET("api/AdminDashboard/GetStation")
    suspend fun getStation(
        @Query("id") id: Int
    ): StationResponse

    @GET("api/AdminDashboard/GetAllStations")
    suspend fun getAllStations(
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("search") search: String? = null
    ): ApiPage<StationResponse>
}
