package com.c242_ps044.artoz.data.remote

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
  @FormUrlEncoded
  @POST("register")
  suspend fun register(
    @Field("name") name: String,
    @Field("email") email: String,
    @Field("password") password: String
  ): Response<ApiResponse<UserStateResponse>>

  @FormUrlEncoded
  @POST("login")
  suspend fun login(
    @Field("email") email: String,
    @Field("password") password: String
  ): Response<ApiResponse<UserStateResponse>>

  @POST("logout")
  suspend fun logout(
    @Header("Authorization") token: String
  ): Response<ApiResponse<Unit>>
}
