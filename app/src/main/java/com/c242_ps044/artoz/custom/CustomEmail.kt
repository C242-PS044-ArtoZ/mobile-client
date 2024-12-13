package com.c242_ps044.artoz.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class CustomEmail : TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun validateEmail(email: String) {
        error = if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Format email tidak valid"
        } else {
            null
        }
    }
}
