package com.c242_ps044.artoz.data.remote

data class TransactionRequest(
  val nominal: Double,
  val type: String,
  val description: String
)