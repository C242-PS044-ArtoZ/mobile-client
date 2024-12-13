package com.c242_ps044.artoz.data.remote

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
  private val sharedPreferences: SharedPreferences =
    context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

  fun saveToken(token: String) {
    sharedPreferences.edit().putString("auth_token", token).apply()
  }

  fun getToken(): String? {
    return sharedPreferences.getString("auth_token", null)
  }

  fun clearToken() {
    sharedPreferences.edit().remove("auth_token").apply()
  }

  fun hasToken(): Boolean {
    return !getToken().isNullOrEmpty()
  }
}
