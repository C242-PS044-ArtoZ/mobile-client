package com.c242_ps044.artoz.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.c242_ps044.artoz.R
import com.c242_ps044.artoz.data.remote.PreferenceManager
import com.c242_ps044.artoz.data.remote.RetrofitClient
import com.c242_ps044.artoz.data.remote.TransactionRepository
import com.c242_ps044.artoz.data.remote.TransactionService
import com.c242_ps044.artoz.databinding.ActivityChartBinding
import com.c242_ps044.artoz.viewmodel.TransactionViewModel
import com.c242_ps044.artoz.viewmodel.TransactionViewModelFactory
import java.text.NumberFormat
import java.util.Locale

class ChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChartBinding
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        observeViewModel()

        // Handle back button click
        binding.btnBack.setOnClickListener {
            finish() // Close the activity
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupViewModel() {
        val transactionService =
            RetrofitClient.provideMainService(this, TransactionService::class.java)
        val repository = TransactionRepository(transactionService, PreferenceManager(this))
        val factory = TransactionViewModelFactory(repository)

        transactionViewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]
        transactionViewModel.getTransactions() // Fetch transactions
    }

    private fun observeViewModel() {
        transactionViewModel.totalIncome.observe(this) { income ->
            updateChartView(income, transactionViewModel.totalExpense.value ?: 0)
            binding.tvIncomeTotal.text = getString(R.string.total_income, formatCurrency(income))
        }

        transactionViewModel.totalExpense.observe(this) { expense ->
            updateChartView(transactionViewModel.totalIncome.value ?: 0, expense)
            binding.tvExpenseTotal.text = getString(R.string.total_expense, formatCurrency(expense))
        }

        transactionViewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateChartView(income: Int, expense: Int) {
        val chartData = mapOf(
            "Pemasukan" to income.toFloat(),
            "Pengeluaran" to expense.toFloat()
        )
        binding.chartView.setData(chartData)
    }

    private fun formatCurrency(amount: Int): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(amount).replace("Rp", "Rp ").replace(",00", "")
    }
}
