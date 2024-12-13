package com.c242_ps044.artoz.data.remote

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

  private const val MAIN_BASE_URL =
    "https://artoz-server-laravel-21101510788.asia-southeast2.run.app/api/"
  private const val OCR_BASE_URL =
    "https://artoz-ocr-flask-21101510788.asia-southeast2.run.app/api/"

  private fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  private fun createClient(context: Context): OkHttpClient {
    val preferenceManager = PreferenceManager(context)
    return OkHttpClient.Builder()
      .addInterceptor(TokenInterceptor(preferenceManager))
      .build()
  }

  /**
   * Provide a generic service for MAIN_BASE_URL
   */
  fun <T> provideMainService(context: Context, serviceClass: Class<T>): T {
    val client = createClient(context)
    val retrofit = createRetrofit(MAIN_BASE_URL, client)
    return retrofit.create(serviceClass)
  }

  /**
   * Provide a generic service for OCR_BASE_URL
   */
  fun <T> provideOcrService(context: Context, serviceClass: Class<T>): T {
    val client = createClient(context)
    val retrofit = createRetrofit(OCR_BASE_URL, client)
    return retrofit.create(serviceClass)
  }
}
