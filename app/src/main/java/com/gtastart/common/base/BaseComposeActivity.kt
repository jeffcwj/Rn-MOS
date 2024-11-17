package com.gtastart.common.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.gtastart.common.theme.GtaStartTheme


abstract class BaseComposeActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GtaStartTheme {
                setContent()
            }
        }
    }

    @Composable
    abstract fun setContent()
}