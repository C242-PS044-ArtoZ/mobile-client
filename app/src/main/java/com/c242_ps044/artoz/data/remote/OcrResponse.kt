package com.c242_ps044.artoz.data.remote

import com.google.gson.annotations.SerializedName

data class OcrResponse(
  @SerializedName("extracted_text")
  val extractedText: String?,

  @SerializedName("total_values")
  val totalValues: String?
)