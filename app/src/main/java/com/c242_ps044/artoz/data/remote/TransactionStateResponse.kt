package com.c242_ps044.artoz.data.remote

import com.google.gson.annotations.SerializedName

data class TransactionStateResponse(
  @field:SerializedName("id")
  val id: String,

  @field:SerializedName("nominal")
  val nominal: Double,

  @field:SerializedName("type")
  val type: String, // "income" atau "expense"

  @field:SerializedName("description")
  val description: String,

  @field:SerializedName("user_id")
  val userId: String,

  @field:SerializedName("created_at")
  val createdAt: String,

  @field:SerializedName("updated_at")
  val updatedAt: String
)

data class TransactionSummaryStateResponse(
  @SerializedName("balance")
  val balance: Double,

  @SerializedName("total_income")
  val totalIncome: String,

  @SerializedName("total_expense")
  val totalExpense: String,

  @SerializedName("period")
  val period: String
)