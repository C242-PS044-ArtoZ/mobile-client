package com.c242_ps044.artoz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c242_ps044.artoz.data.remote.AuthRepository

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
      return AuthViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
