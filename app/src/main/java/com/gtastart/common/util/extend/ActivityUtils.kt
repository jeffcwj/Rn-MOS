package com.gtastart.common.util.extend

import android.app.Activity
import android.content.Context
import android.content.Intent

fun<A: Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

inline fun <reified T> StartActivity(
    context: Context
) {
    val intent = Intent(context, T::class.java)
    context.startActivity(intent)
}