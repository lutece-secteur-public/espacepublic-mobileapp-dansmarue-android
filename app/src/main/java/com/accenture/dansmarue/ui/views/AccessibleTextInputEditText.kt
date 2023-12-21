package com.accenture.dansmarue.ui.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class AccessibleTextInputEditText(context: Context, attrs: AttributeSet) :
        AppCompatEditText(context, attrs) {

    private var maxLength: Int = -1

    init {
        val a = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.maxLength))
        maxLength = a.getInt(0, -1)
        a.recycle()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == maxLength) {
                    announceForAccessibility("Limite de caractères atteinte.")
                }
            }
        })
    }
}