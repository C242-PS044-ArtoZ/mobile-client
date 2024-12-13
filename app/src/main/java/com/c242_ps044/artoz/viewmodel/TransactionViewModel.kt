package com.c242_ps044.artoz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c242_ps044.artoz.data.remote.TransactionRepository
import com.c242_ps044.artoz.data.remote.TransactionRequest
import com.c242_ps044.artoz.data.remote.TransactionStateResponse
import com.c242_ps044.artoz.data.remote.TransactionSummaryStateResponse
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _transactions = MutableLiveData<List<TransactionStateResponse>?>()
    val transactions: LiveData<List<TransactionStateResponse>?> get() = _transactions

    private val _storeTransactionResult = MutableLiveData<TransactionStateResponse?>()
    val storeTransactionResult: LiveData<TransactionStateResponse?> get() = _storeTransactionResult

    private val _deleteTransactionResult = MutableLiveData<Boolean>()
    val deleteTransactionResult: LiveData<Boolean> get() = _deleteTransactionResult

    private val _totalIncome = MutableLiveData<Int>()
    val totalIncome: LiveData<Int> get() = _totalIncome

    private val _totalExpense = MutableLiveData<Int>()
    val totalExpense: LiveData<Int> get() = _totalExpense

    private val _transactionSummary = MutableLiveData<TransactionSummaryStateResponse?>()
    val transactionSummary: LiveData<TransactionSummaryStateResponse?> get() = _transactionSummary

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    var lastDeletedTransactionId: String? =
        null // Properti untuk menyimpan ID transaksi terakhir yang dihapus

    // Fetch all transactions and calculate totals
    fun getTransactions() {
        viewModelScope.launch {
            val result = repository.getTransactions()
            if (result.isSuccess) {
                val transactionList = result.getOrNull()
                _transactions.value = transactionList
                calculateTotals(transactionList ?: emptyList())
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Calculate total income and expense
    private fun calculateTotals(transactions: List<TransactionStateResponse>) {
        val income = transactions.filter { it.type == "income" }
            .sumOf { it.nominal.toInt() }
        val expense = transactions.filter { it.type == "expense" }
            .sumOf { it.nominal.toInt() }

        _totalIncome.value = income
        _totalExpense.value = expense
    }

    // Store a new transaction
    fun storeTransaction(transactionRequest: TransactionRequest) {
        viewModelScope.launch {
            val result = repository.storeTransaction(transactionRequest)
            if (result.isSuccess) {
                _storeTransactionResult.value = result.getOrNull()
                getTransactions() // Refresh transactions list
                refreshTransactionSummary() // Refresh transaction summary
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    // Delete a transaction
    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            lastDeletedTransactionId = transactionId // Simpan ID transaksi yang dihapus
            val result = repository.deleteTransaction(transactionId)
            if (result.isSuccess) {
                _deleteTransactionResult.value = true
                getTransactions() // Refresh transactions list
                refreshTransactionSummary() // Refresh transaction summary
            } else {
                _deleteTransactionResult.value = false
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun getTransactionSummary(period: String) {
        viewModelScope.launch {
            val result = repository.getTransactionSummary(period)
            if (result.isSuccess) {
                _transactionSummary.value = result.getOrNull()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun refreshTransactionSummary(period: String = "month") {
        viewModelScope.launch {
            val result = repository.getTransactionSummary(period)
            if (result.isSuccess) {
                _transactionSummary.value = result.getOrNull()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }
}
