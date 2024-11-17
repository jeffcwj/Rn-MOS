package com.gtastart.common.util.compose

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

/**
 * 高度自动匹配子控件最大高度，类似 wrap_content，和wrapContentHeight区别是，若子控件设置fillMaxHeight()，IntrinsicSize.Mi
 */
fun Modifier.matchContentHeight() = this.then(
        this.height(IntrinsicSize.Min)
)

/**
 * 宽度自动匹配子控件最大宽度，类似 wrap_content
 */
fun Modifier.matchContentWidth() = this.then(
        this.width(IntrinsicSize.Min)
)

@Composable
fun Modifier.mPlaceholder(
        visible: Boolean
) = this.then(
        this.placeholder(
                visible = visible,
                highlight = PlaceholderHighlight.fade()
        )
)