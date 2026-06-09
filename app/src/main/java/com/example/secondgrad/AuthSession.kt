package com.example.secondgrad

import android.content.Context

object AuthSession {

    private const val PREFS_NAME = "auth_session"
    private const val KEY_TOKEN = "token"
    private const val KEY_EMAIL = "email"
    private const val KEY_FULL_NAME = "full_name"
    private const val KEY_USER_ID = "user_id"

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun saveAuth(response: AuthResponse) {
        val token = response.token
        if (token == null || token.length == 0) {
            return
        }

        val context = appContext ?: return
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_EMAIL, response.email)
        editor.putString(KEY_FULL_NAME, response.fullName)
        if (response.userId != null) {
            editor.putInt(KEY_USER_ID, response.userId)
        }
        editor.apply()
    }

    fun getToken(): String? {
        val context = appContext ?: return null
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)
    }

    fun getEmail(): String? {
        val context = appContext ?: return null
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, null)
    }

    fun getFullName(): String? {
        val context = appContext ?: return null
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_FULL_NAME, null)
    }

    fun getAuthorizationHeader(): String? {
        val token = getToken()
        if (token == null || token.length == 0) {
            return null
        }
        return "Bearer $token"
    }

    fun clear() {
        val context = appContext ?: return
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
