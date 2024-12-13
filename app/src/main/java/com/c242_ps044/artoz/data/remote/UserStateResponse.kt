package com.c242_ps044.artoz.data.remote

import com.google.gson.annotations.SerializedName

data class UserStateResponse(
  @field:SerializedName("updated_at")
  val updatedAt: String,

  @field:SerializedName("name")
  val name: String,

  @field:SerializedName("created_at")
  val createdAt: String,

  @field:SerializedName("id")
  val id: String,

  @field:SerializedName("email")
  val email: String
)
