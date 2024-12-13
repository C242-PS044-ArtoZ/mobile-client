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
import com.c242_ps044.artoz.databinding.ActivityRegisterBinding
import com.c242_ps044.artoz.viewmodel.AuthViewModel
import com.c242_ps044.artoz.viewmodel.AuthViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    // Menggunakan AuthService dari RetrofitClient
    private val authViewModel: AuthViewModel by viewModels {
        val preferenceManager = PreferenceManager(this)
        val authService = RetrofitClient.provideMainService(this, AuthService::class.java)
        val repository = AuthRepository(authService, preferenceManager)
        AuthViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()

        // Tombol Register
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmailRegist.text.toString()
            val password = binding.etPasswordRegist.text.toString()

            // Validasi input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressBar.visibility = View.VISIBLE
                authViewModel.register(name, email, password)
            }
        }

        // Navigasi ke Login
        binding.tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            startActivity(intent, options.toBundle())
        }
    }

    private fun observeViewModel() {
        // Observasi state registrasi
        authViewModel.registerState.observe(this) { user ->
            binding.progressBar.visibility = View.GONE
            if (user != null) {
                Toast.makeText(this, "Registrasi berhasil, ${user.name}", Toast.LENGTH_SHORT).show()

                // Direct ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Menutup RegisterActivity
            }
        }

        // Observasi error
        authViewModel.error.observe(this) { errorMessage ->
            binding.progressBar.visibility = View.GONE
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
