package com.gtastart.common.util.extend

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.Snackbar(message: String, actionText: String? = null, action: ((View) -> Unit)? = null, isLong: Boolean = false) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)

    if (actionText != null && action != null) {
        snackbar.setAction(actionText) { view ->
            action(view)
        }
    }
    snackbar.show()
}

fun View.Visible(isVisible: Boolean) {
    if (isVisible) View.VISIBLE else View.GONE
}