package com.example.secondgrad

class AuthRepository {

    private val api = TransGuideRetrofit.api

    suspend fun signIn(email: String, password: String): AuthResponse {
        return api.signIn(
            SignInRequest(
                email = email,
                password = password
            )
        )
    }

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        country: String,
        address: String
    ): AuthResponse {
        return api.signUp(
            SignUpRequest(
                email = email,
                password = password,
                fullName = fullName,
                country = country,
                address = address
            )
        )
    }

    suspend fun sendResetCode(email: String): String {
        val responseBody = api.sendResetCode(EmailRequest(email = email))
        return responseBody.string()
    }

    suspend fun confirmResetCode(email: String, code: String): String {
        val responseBody = api.confirmResetCode(
            VerifyResetCodeRequest(
                email = email,
                code = code
            )
        )
        return responseBody.string()
    }

    suspend fun resetPassword(
        email: String,
        password: String,
        confirmPassword: String
    ): String {
        val responseBody = api.resetPassword(
            ResetPasswordRequest(
                email = email,
                password = password,
                confirmPassword = confirmPassword
            )
        )
        return responseBody.string()
    }
}
