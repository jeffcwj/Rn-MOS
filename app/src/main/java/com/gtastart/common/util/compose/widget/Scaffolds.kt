package com.gtastart.common.util.compose.widget

import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.LoadState
import com.valvesoftware.source.R

/**
 * 垂直上划加载更多&下拉刷新脚手架
 */
@Composable
fun <T: Any> VerticalLoadMoreListScaffold(
    modifier: Modifier = Modifier,
    isNestScroll: Boolean = true, // 是否支持嵌套滚动
    adaptiveColumnSize: Dp = 300.dp, // 每一项的宽度阈值，用于自动判断一行的项数
    list: List<T>?, // 列表数据
    itemLayout: @Composable ((index: Int, item: T) -> Unit), // 列表项UI
    onLoadMore: () -> Unit, // 上划触发的加载更多事件
    isRefreshingOrLastPage: Boolean, // 划到底部是否加载的额外判断
    loadState: MutableState<LoadState>, // 划到底部决定是否加载的加载状态
    loadMoreTrailingLayout: @Composable () -> Unit, // 尾部加载更多进度条UI
    ) {
    val lazyListState = rememberLazyStaggeredGridState()

    // 监听滚动状态 TODO: 有点小毛病，第一次启动时即使没到底部也会触发一次
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val totalItemCount = layoutInfo.totalItemsCount
                val lastVisibleItemInfo = layoutInfo.visibleItemsInfo.lastOrNull()

                // 检查是否到达底部
                val isLastItemVisible = lastVisibleItemInfo?.index == totalItemCount - 1
                if (!isLastItemVisible) {
                    return@collect
                }

                if (loadState.value == LoadState.NotLoading && !isRefreshingOrLastPage) {
                    Log.d("", "ResItems: 触发加载更多")
                    loadState.value = LoadState.Loading
                    onLoadMore.invoke()
                }
            }
    }

    LazyVerticalStaggeredGrid(
        modifier = if (isNestScroll) modifier.nestedScroll(rememberNestedScrollInteropConnection()) else modifier,
        state = lazyListState,
        columns = StaggeredGridCells.Adaptive(adaptiveColumnSize),
        contentPadding = PaddingValues(GtaStartTheme.spacing.normal),
        verticalItemSpacing = GtaStartTheme.spacing.normal,
        horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal),
    ) {
        itemsIndexed(list?.toList()?: emptyList()) { index, item ->
            itemLayout.invoke(index, item) // 列表项UI
        }
        item {
            loadMoreTrailingLayout.invoke() // 尾部加载更多UI
        }
    }


}


@Composable
fun CollapsingScaffold(
    modifier: Modifier = Modifier,
    collapsingContent: @Composable () -> Unit,
    scrollContent: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        AndroidView(
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.compose_collapsing, null)
            },
            modifier = Modifier.fillMaxWidth()
        ) { view ->
            val linearlayout = view.findViewById<LinearLayout>(R.id.linearlayout)
            val composeView1 = ComposeView(view.context).apply {
                setContent {
                    collapsingContent.invoke()
                }
            }
            linearlayout.addView(composeView1)

            val constraintLayout = view.findViewById<ConstraintLayout>(R.id.constraintLayout)
            val composeView2 = ComposeView(view.context).apply {
                setContent {
                    scrollContent.invoke()
                }
            }
            constraintLayout.addView(composeView2)
        }
    }
}