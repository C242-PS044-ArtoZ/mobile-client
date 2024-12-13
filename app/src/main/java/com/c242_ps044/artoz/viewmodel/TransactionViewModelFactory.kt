package com.c242_ps044.artoz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c242_ps044.artoz.data.remote.TransactionRepository

class TransactionViewModelFactory(
  private val repository: TransactionRepository
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
      return TransactionViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
