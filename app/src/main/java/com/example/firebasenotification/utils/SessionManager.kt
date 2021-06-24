package com.example.firebasenotification.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import androidx.core.content.edit
import com.example.firebasenotification.BuildConfig

object SessionManager {
    private const val prefName = "${BuildConfig.APPLICATION_ID}.session"
    private lateinit var pref: SharedPreferences

    fun init(@NonNull context: Context) {
        pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    fun createSession(name: String?, email: String?, religion: String?, gender: String?, password: String) {

        pref.edit {
            putBoolean("isLogin", true)
            putString("username", name)
            putString("email", email)
            putString("religion", religion)
            putString("gender", gender)
            putString("password", password)
        }
    }

    fun clearSession() {
        pref.edit {
            clear()
        }
    }

    var fcmToken: String
        get() {
            return pref.getString("fcmToken", "")!!
        }
        set(value) {
            pref.edit {
                putString("fcmToken", value)
            }
        }
}