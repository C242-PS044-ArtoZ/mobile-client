package com.c242_ps044.artoz.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val preferenceManager: PreferenceManager) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val token = preferenceManager.getToken()
    val request = chain.request().newBuilder()
      .apply {
        if (!token.isNullOrEmpty()) {
          addHeader("Authorization", "Bearer $token")
        }
      }
      .build()
    return chain.proceed(request)
  }
}
