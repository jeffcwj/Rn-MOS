package com.gtastart.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.gtastart.common.theme.GtaStartTheme

abstract class BaseComposeFragment : Fragment() {

    protected lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        composeView = ComposeView(requireContext())
        composeView.setContent {
            GtaStartTheme {
                setContent()
            }
        }
        return composeView
    }

    @Composable
    abstract fun setContent()
}