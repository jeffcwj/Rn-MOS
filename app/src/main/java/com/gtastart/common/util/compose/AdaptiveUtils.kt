package com.gtastart.common.util.compose

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun getWindowSize(): DpSize {
    return with(LocalDensity.current) {
        currentWindowSize().toSize().toDpSize()
    }
}

@Composable
fun getLayoutType(): NavigationSuiteType {
    return NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfoFix(currentWindowAdaptiveInfo())
}

/**
 * 导航栏类型自适应判断 修改版
 */
@Composable
fun NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfoFix(adaptiveInfo: WindowAdaptiveInfo): NavigationSuiteType {
    // TODO: 还需要调整适应性
    return with(adaptiveInfo) {
        val windowSize = with(LocalDensity.current) {
            currentWindowSize().toSize().toDpSize()
        }
//        val vertical = windowSize.height > windowSize.width
//        val horizontal = windowSize.height <= windowSize.width
        if ( windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
            windowSize.width > 1200.dp) { // 使用WindowWidthSizeClass判断范围有点问题，只能硬编码判断。不过其实也不太需要NavigationDrawer，太占地方但是又不能自定义
            NavigationSuiteType.NavigationDrawer
        } else if (windowPosture.isTabletop) {
            NavigationSuiteType.NavigationBar
        } else if (
            windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
            windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
        ) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteType.NavigationBar
        }
    }
}


/*@Composable
fun GetAdaptiveRowItemCount(adaptiveInfo: WindowAdaptiveInfo, windowSize: DpSize) : Int {
    LazyVerticalGrid() { }
    GridCells.Adaptive

    val widthSize = adaptiveInfo.windowSizeClass.windowWidthSizeClass
    if (widthSize == WindowWidthSizeClass.EXPANDED)
}*/


fun Density.calculateRowItemCount(availableSize: Int, spacing: Dp, minSize: Dp) : Int {
    val count = maxOf((availableSize + spacing.roundToPx()) / (minSize.roundToPx() + spacing.roundToPx()), 1)
    return count
}