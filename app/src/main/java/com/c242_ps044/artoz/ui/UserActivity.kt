package com.c242_ps044.artoz.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c242_ps044.artoz.R
import com.c242_ps044.artoz.data.remote.PreferenceManager
import com.c242_ps044.artoz.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

  private lateinit var binding: ActivityUserBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Inisialisasi ViewBinding
    binding = ActivityUserBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Inisialisasi PreferenceManager
    val preferenceManager = PreferenceManager(this)

    // Periksa apakah sudah ada token
    if (preferenceManager.getToken() != null) {
      // Jika token ada, arahkan langsung ke MainActivity
      navigateToMainActivity()
      return
    }

    // Listener untuk tombol Login
    binding.btnLogin.setOnClickListener {
      val intent = Intent(this, LoginActivity::class.java)
      val options = ActivityOptions.makeCustomAnimation(
        this,
        R.anim.slide_in_left,
        R.anim.slide_out_right
      )
      startActivity(intent, options.toBundle())
    }

    // Listener untuk tombol Register
    binding.btnRegister.setOnClickListener {
      val intent = Intent(this, RegisterActivity::class.java)
      val options = ActivityOptions.makeCustomAnimation(
        this,
        R.anim.slide_in_right,
        R.anim.slide_out_left
      )
      startActivity(intent, options.toBundle())
    }
  }

  private fun navigateToMainActivity() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
  }
}
