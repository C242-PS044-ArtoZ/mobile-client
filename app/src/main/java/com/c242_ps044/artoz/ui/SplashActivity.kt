package com.c242_ps044.artoz.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.c242_ps044.artoz.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    val logo = findViewById<ImageView>(R.id.iv_logo)
    val zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
    logo.startAnimation(zoomIn)

    Handler(Looper.getMainLooper()).postDelayed({
      val intent = Intent(this, UserActivity::class.java)
      startActivity(intent)
      finish()
    }, 1500)
  }
}