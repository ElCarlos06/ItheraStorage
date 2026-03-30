package com.example.activos360.core.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject

object TokenManager {
    private const val PREFS_NAME = "activos360_prefs"
    private const val KEY_JWT = "jwt"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        check(::prefs.isInitialized) { "TokenManager.init(context) must be called first" }
        prefs.edit().putString(KEY_JWT, token).apply()
    }

    fun getToken(): String? {
        if (!::prefs.isInitialized) return null
        return prefs.getString(KEY_JWT, null)
    }

    fun clear() {
        if (!::prefs.isInitialized) return
        prefs.edit().remove(KEY_JWT).apply()
    }

    /**
     * Backend incluye el claim "id" en el JWT. Si no está, regresa null.
     */
    fun getUserIdFromToken(): Long? {
        val token = getToken() ?: return null
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
            val json = JSONObject(payload)
            when (val idValue = json.opt("id")) {
                is Number -> idValue.toLong()
                is String -> idValue.toLongOrNull()
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun isTokenValid(): Boolean {
        val token = getToken() ?: return false
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return false
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
            val exp = JSONObject(payload).optLong("exp", 0L)
            exp > System.currentTimeMillis() / 1000
        } catch (_: Exception) {
            false
        }
    }

    fun getNombreFromToken(): String? = getClaimString("nombre")

    fun getRoleFromToken(): String? = getClaimString("role")

    fun getCorreoFromToken(): String? {
        val token = getToken() ?: return null
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
            JSONObject(payload).optString("sub", null)
        } catch (_: Exception) {
            null
        }
    }

    private fun getClaimString(claim: String): String? {
        val token = getToken() ?: return null
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
            JSONObject(payload).optString(claim, null)
        } catch (_: Exception) {
            null
        }
    }
}

