package com.c242_ps044.artoz.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface TransactionService {
  @GET("transactions")
  suspend fun getTransactions(@Header("Authorization") token: String): Response<ApiResponse<List<TransactionStateResponse>>>

  @POST("transactions")
  suspend fun storeTransactions(
    @Header("Authorization") token: String,
    @Body transactionRequest: TransactionRequest
  ): Response<ApiResponse<TransactionStateResponse>>

  @DELETE("transactions/{id}")
  suspend fun deleteTransaction(
    @Header("Authorization") token: String,
    @Path("id") transactionId: String
  ): Response<Unit>

  @GET("transactions-summary")
  suspend fun getTransactionSummary(
    @Header("Authorization") token: String,
    @Query("period") period: String
  ): Response<ApiResponse<TransactionSummaryStateResponse>>
}