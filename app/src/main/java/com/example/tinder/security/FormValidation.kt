package com.example.tinder.security

import android.widget.EditText
import java.util.regex.Pattern

fun isValidMobile(phone: String): Boolean {
    return if (!Pattern.matches("^[+91][0-9]{10}$", phone)) {
        phone.length == 10
    } else false
}

fun isEmpty(etText: EditText): Boolean {
    return etText.text.toString().trim { it <= ' ' }.isEmpty()
}
