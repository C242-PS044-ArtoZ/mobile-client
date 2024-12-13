package com.c242_ps044.artoz.data.remote

class AuthRepository(
  private val authService: AuthService,
  private val preferenceManager: PreferenceManager
) {

  suspend fun login(email: String, password: String): Result<UserStateResponse> {
    return try {
      val response = authService.login(email, password)
      if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
          body.token?.let { preferenceManager.saveToken(it) }
          Result.success(body.data)
        } else {
          Result.failure(Exception("No response body"))
        }
      } else {
        Result.failure(Exception("Error: ${response.errorBody()?.string() ?: response.message()}"))
      }
    } catch (e: Exception) {
      Result.failure(Exception("Login gagal: ${e.localizedMessage}"))
    }
  }

  suspend fun register(name: String, email: String, password: String): Result<UserStateResponse> {
    return try {
      val response = authService.register(name, email, password)
      if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
          body.token?.let { preferenceManager.saveToken(it) }
          Result.success(body.data)
        } else {
          Result.failure(Exception("No response body"))
        }
      } else {
        Result.failure(Exception("Error: ${response.errorBody()?.string() ?: response.message()}"))
      }
    } catch (e: Exception) {
      Result.failure(Exception("Registrasi gagal: ${e.localizedMessage}"))
    }
  }

  suspend fun logout(): Result<Boolean> {
    val token = preferenceManager.getToken()
    return if (token != null) {
      try {
        val response = authService.logout("Bearer $token")
        if (response.isSuccessful) {
          preferenceManager.clearToken() // Hapus token jika logout berhasil
          Result.success(true)
        } else {
          Result.failure(
            Exception(
              "Logout gagal: ${
                response.errorBody()?.string() ?: response.message()
              }"
            )
          )
        }
      } catch (e: Exception) {
        Result.failure(Exception("Terjadi kesalahan saat logout: ${e.localizedMessage}"))
      }
    } else {
      Result.failure(Exception("Token tidak ditemukan. Silakan login kembali."))
    }
  }
}
