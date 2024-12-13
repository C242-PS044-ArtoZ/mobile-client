package com.c242_ps044.artoz.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.c242_ps044.artoz.R
import com.c242_ps044.artoz.data.remote.AuthRepository
import com.c242_ps044.artoz.data.remote.AuthService
import com.c242_ps044.artoz.data.remote.PreferenceManager
import com.c242_ps044.artoz.data.remote.RetrofitClient
import com.c242_ps044.artoz.databinding.ActivityLoginBinding
import com.c242_ps044.artoz.viewmodel.AuthViewModel
import com.c242_ps044.artoz.viewmodel.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {

  private lateinit var binding: ActivityLoginBinding

  // Menggunakan ViewModelFactory untuk membuat AuthViewModel
  private val authViewModel: AuthViewModel by viewModels {
    val preferenceManager = PreferenceManager(this)
    val authService = RetrofitClient.provideMainService(this, AuthService::class.java)
    val repository = AuthRepository(authService, preferenceManager)
    AuthViewModelFactory(repository)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Inisialisasi ViewBinding
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupListeners()
    observeViewModel()
  }

  private fun setupListeners() {
    // Tombol Login
    binding.btnLogin.setOnClickListener {
      val email = binding.etEmail.text.toString()
      val password = binding.etPassword.text.toString()

      if (email.isNotEmpty() && password.isNotEmpty()) {
        binding.progressBar.visibility = View.VISIBLE
        authViewModel.login(email, password)
      } else {
        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
      }
    }

    // Tombol untuk pindah ke Register
    binding.tvToRegist.setOnClickListener {
      val intent = Intent(this, RegisterActivity::class.java)
      val options = ActivityOptions.makeCustomAnimation(
        this,
        R.anim.slide_in_right,
        R.anim.slide_out_left
      )
      startActivity(intent, options.toBundle())
    }
  }

  private fun observeViewModel() {
    // Observasi loginState
    authViewModel.loginState.observe(this) { user ->
      binding.progressBar.visibility = View.GONE
      if (user != null) {
        Toast.makeText(this, "Welcome, ${user.name}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
      }
    }

    // Observasi error
    authViewModel.error.observe(this) { errorMessage ->
      binding.progressBar.visibility = View.GONE
      if (!errorMessage.isNullOrEmpty()) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
      }
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
  }
}
