package com.example.firebasenotification.utils

import android.content.Context
import android.widget.Toast
import timber.log.Timber

fun Context.toast(msg: String?, time: Int = Toast.LENGTH_SHORT) {
    if (!msg.isNullOrEmpty())
        Toast.makeText(this, msg, time).show()
}

fun Context.timber(msg: String?) {
    if (!msg.isNullOrEmpty()) {
        Timber.d(msg)
    }
}
