package com.c242_ps044.artoz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps044.artoz.data.remote.AuthRepository
import com.c242_ps044.artoz.data.remote.UserStateResponse
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

  private val _registerState = MutableLiveData<UserStateResponse?>()
  val registerState: LiveData<UserStateResponse?> get() = _registerState

  private val _error = MutableLiveData<String?>()
  val error: LiveData<String?> get() = _error

  fun register(name: String, email: String, password: String) {
    viewModelScope.launch {
      val result = authRepository.register(name, email, password)
      if (result.isSuccess) {
        _registerState.value = result.getOrNull()
      } else {
        _error.value = result.exceptionOrNull()?.message
      }
    }
  }

  private val _loginState = MutableLiveData<UserStateResponse?>()
  val loginState: LiveData<UserStateResponse?> get() = _loginState

  fun login(email: String, password: String) {
    viewModelScope.launch {
      val result = authRepository.login(email, password)
      if (result.isSuccess) {
        _loginState.value = result.getOrNull()
      } else {
        _error.value = result.exceptionOrNull()?.message
      }
    }
  }

  private val _logoutState = MutableLiveData<Boolean>()
  val logoutState: LiveData<Boolean> get() = _logoutState

  fun logout() {
    viewModelScope.launch {
      val result = authRepository.logout()
      if (result.isSuccess) {
        _logoutState.value = true
      } else {
        _error.value = result.exceptionOrNull()?.message
      }
    }
  }
}
