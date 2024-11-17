package com.gtastart.common.util

import android.content.Context
import android.widget.Toast


fun Context.MToast(msg: String, isLong: Boolean = false) {
//    MToast.show(this, msg, isLong)
    Toast.makeText(this, msg, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}