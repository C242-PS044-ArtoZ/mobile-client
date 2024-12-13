package com.c242_ps044.artoz.data.remote

class TransactionRepository(
  private val transactionService: TransactionService,
  private val preferenceManager: PreferenceManager
) {

  suspend fun getTransactions(): Result<List<TransactionStateResponse>> {
    val token = preferenceManager.getToken()
    return if (token != null) {
      try {
        val response = transactionService.getTransactions("Bearer $token")
        if (response.isSuccessful) {
          val body = response.body()
          if (body != null) {
            Result.success(body.data)
          } else {
            Result.failure(Exception("No response body"))
          }
        } else {
          Result.failure(Exception("Error: ${response.message()}"))
        }
      } catch (e: Exception) {
        Result.failure(e)
      }
    } else {
      Result.failure(Exception("Token is missing"))
    }
  }

  suspend fun storeTransaction(transactionRequest: TransactionRequest): Result<TransactionStateResponse> {
    val token = preferenceManager.getToken()
    return if (token != null) {
      try {
        val response =
          transactionService.storeTransactions("Bearer $token", transactionRequest)
        if (response.isSuccessful) {
          val body = response.body()
          if (body != null) {
            Result.success(body.data)
          } else {
            Result.failure(Exception("No response body"))
          }
        } else {
          Result.failure(Exception("Error: ${response.message()}"))
        }
      } catch (e: Exception) {
        Result.failure(e)
      }
    } else {
      Result.failure(Exception("Token is missing"))
    }
  }

  suspend fun deleteTransaction(transactionId: String): Result<Unit> {
    val token = preferenceManager.getToken()
    return if (token != null) {
      try {
        val response = transactionService.deleteTransaction("Bearer $token", transactionId)
        if (response.isSuccessful) {
          Result.success(Unit)
        } else {
          Result.failure(Exception("Error: ${response.message()}"))
        }
      } catch (e: Exception) {
        Result.failure(e)
      }
    } else {
      Result.failure(Exception("Token is missing"))
    }
  }

  suspend fun getTransactionSummary(period: String): Result<TransactionSummaryStateResponse> {
    val token = preferenceManager.getToken()
    return if (token != null) {
      try {
        val response = transactionService.getTransactionSummary("Bearer $token", period)
        if (response.isSuccessful) {
          val body = response.body()
          if (body != null) {
            Result.success(body.data)
          } else {
            Result.failure(Exception("No response body"))
          }
        } else {
          Result.failure(Exception("Error: ${response.message()}"))
        }
      } catch (e: Exception) {
        Result.failure(e)
      }
    } else {
      Result.failure(Exception("Token is missing"))
    }
  }

}
