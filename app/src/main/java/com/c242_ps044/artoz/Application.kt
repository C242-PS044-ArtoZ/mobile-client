package com.c242_ps044.artoz

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Set aplikasi ke Light Mode secara global
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
