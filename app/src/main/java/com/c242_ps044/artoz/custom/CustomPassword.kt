package com.c242_ps044.artoz.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.c242_ps044.artoz.R
import com.google.android.material.textfield.TextInputEditText

class CustomPassword : TextInputEditText, View.OnTouchListener {

    private var isPasswordVisible = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        // Atur input type default menjadi password
        transformationMethod = PasswordTransformationMethod.getInstance()

        // Tambahkan ikon visibility di sebelah kanan
        setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24),
            null
        )

        // Tambahkan listener untuk melihat/mengubah visibilitas password
        setOnTouchListener(this)

        // Tambahkan validasi panjang password
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error = if (s != null && s.length < 8) {
                    "Password minimal 8 karakter"
                } else {
                    null
                }
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val drawableEnd = compoundDrawables[2] // Ikon di sebelah kanan
            if (drawableEnd != null && event.rawX >= (right - drawableEnd.bounds.width())) {
                togglePasswordVisibility()
                return true
            }
        }
        return false
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        transformationMethod = if (isPasswordVisible) {
            null // Tampilkan password
        } else {
            PasswordTransformationMethod.getInstance() // Sembunyikan password
        }
        setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            ContextCompat.getDrawable(
                context,
                if (isPasswordVisible) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24
            ),
            null
        )
        setSelection(text?.length ?: 0) // Pindahkan cursor ke akhir teks
    }
}
