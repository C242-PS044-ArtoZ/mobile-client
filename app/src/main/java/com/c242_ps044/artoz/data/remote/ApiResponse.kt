package com.c242_ps044.artoz.data.remote

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(

  @field:SerializedName("data")
  val data: T,

  @field:SerializedName("message")
  val message: String,

  @field:SerializedName("status")
  val status: String,

  @field:SerializedName("token")
  val token: String? = null
)