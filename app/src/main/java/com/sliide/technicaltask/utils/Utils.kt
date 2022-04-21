package com.sliide.technicaltask.utils

import android.widget.TextView
import java.util.regex.Pattern

object Utils {
    val EMAIL_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun setText(textView: TextView, value: String) {
        textView.text = value
    }

    fun validateEmailAddress(email: String): Boolean {
        return !email.isNullOrEmpty() && EMAIL_PATTERN.matcher(email).matches()
    }
}