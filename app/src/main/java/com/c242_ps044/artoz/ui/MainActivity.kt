package com.c242_ps044.artoz.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.c242_ps044.artoz.ListItem
import com.c242_ps044.artoz.R
import com.c242_ps044.artoz.adapter.ListAdapter
import com.c242_ps044.artoz.data.remote.AuthRepository
import com.c242_ps044.artoz.data.remote.AuthService
import com.c242_ps044.artoz.data.remote.PreferenceManager
import com.c242_ps044.artoz.data.remote.RetrofitClient
import com.c242_ps044.artoz.data.remote.TransactionRepository
import com.c242_ps044.artoz.data.remote.TransactionService
import com.c242_ps044.artoz.data.remote.TransactionStateResponse
import com.c242_ps044.artoz.databinding.ActivityMainBinding
import com.c242_ps044.artoz.viewmodel.AuthViewModel
import com.c242_ps044.artoz.viewmodel.AuthViewModelFactory
import com.c242_ps044.artoz.viewmodel.TransactionViewModel
import com.c242_ps044.artoz.viewmodel.TransactionViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var authViewModel: AuthViewModel
    private lateinit var transactionViewModel: TransactionViewModel

    private val addActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize components
        preferenceManager = PreferenceManager(this)
        val authRepository = AuthRepository(
            RetrofitClient.provideMainService(this, AuthService::class.java),
            preferenceManager
        )
        authViewModel = AuthViewModelFactory(authRepository).create(AuthViewModel::class.java)

        val transactionRepository = TransactionRepository(
            RetrofitClient.provideMainService(this, TransactionService::class.java),
            preferenceManager
        )
        transactionViewModel = TransactionViewModelFactory(transactionRepository)
            .create(TransactionViewModel::class.java)

        setupRecyclerView()
        setupFab()
        observeViewModel()

        // Initial data fetch
        refreshData()

        // Setup manual refresh
        setupManualRefresh()
    }

    private fun setupRecyclerView() {
        binding.rvRecentHistory.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFab() {
        val fabMenu: FloatingActionButton = binding.fabMenu
        fabMenu.setOnClickListener {
            animateFab(fabMenu)
            showFabMenu()
        }
    }

    private fun animateFab(fab: FloatingActionButton) {
        fab.animate()
            .rotationBy(360f)
            .setDuration(300)
            .start()
    }

    private fun showFabMenu() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_menu, null)
        bottomSheetDialog.setContentView(view)

        view.findViewById<View>(R.id.nav_add).setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            addActivityLauncher.launch(intent)
            bottomSheetDialog.dismiss()
        }
        view.findViewById<View>(R.id.nav_chart).setOnClickListener {
            val intent = Intent(this, ChartActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }
        view.findViewById<View>(R.id.nav_logout).setOnClickListener {
            showLogoutConfirmationDialog()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Logout") { dialog, _ ->
                dialog.dismiss()
                authViewModel.logout()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun setupManualRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        showLoading(true)
        transactionViewModel.getTransactions()
    }

    private fun observeViewModel() {
        authViewModel.logoutState.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        transactionViewModel.transactions.observe(this) { transactions ->
            showLoading(false)
            binding.swipeRefreshLayout.isRefreshing = false
            if (transactions.isNullOrEmpty()) {
                binding.tvNoTransactions.visibility = View.VISIBLE
                binding.rvRecentHistory.visibility = View.GONE
                // Set summary ke 0 saat data kosong
                updateSummary(0, 0, 0)
            } else {
                binding.tvNoTransactions.visibility = View.GONE
                binding.rvRecentHistory.visibility = View.VISIBLE
                binding.rvRecentHistory.adapter = ListAdapter(transactions.map {
                    ListItem(it.id, it.type, it.description, it.nominal.toInt())
                }) { item ->
                    showDeleteConfirmationDialog(item)
                }

                calculateAndUpdateSummary(transactions)
            }
        }

        transactionViewModel.deleteTransactionResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Transaksi berhasil dihapus!", Toast.LENGTH_SHORT).show()
                val updatedTransactions = transactionViewModel.transactions.value?.filter {
                    it.id != transactionViewModel.lastDeletedTransactionId
                }
                updatedTransactions?.let {
                    calculateAndUpdateSummary(it)
                }
                refreshData() // Refresh data to ensure consistency
            } else {
                Toast.makeText(this, "Gagal menghapus transaksi", Toast.LENGTH_SHORT).show()
            }
        }

        transactionViewModel.error.observe(this) { errorMessage ->
            showLoading(false)
            binding.swipeRefreshLayout.isRefreshing = false
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateAndUpdateSummary(transactions: List<TransactionStateResponse>) {
        val income = transactions.filter { it.type == "income" }.sumOf { it.nominal.toInt() }
        val expense = transactions.filter { it.type == "expense" }.sumOf { it.nominal.toInt() }
        val balance = income - expense

        updateSummary(income, expense, balance)
    }

    private fun updateSummary(income: Int, expense: Int, balance: Int) {
        binding.tvIncomeNominal.text = formatCurrency(income)
        binding.tvExpenseNominal.text = formatCurrency(expense)
        binding.tvBalanceNominal.text = formatCurrency(balance)
    }

    private fun showDeleteConfirmationDialog(item: ListItem) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus transaksi ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                dialog.dismiss()
                transactionViewModel.deleteTransaction(item.id)
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun formatCurrency(amount: Int): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(amount).replace("Rp", "Rp ").replace(",00", "")
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
