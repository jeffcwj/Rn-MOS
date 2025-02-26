package com.billflx.csgo.page.test

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gtastart.common.util.compose.widget.ComposeRecyclerView
import com.gtastart.common.util.compose.widget.MButton
import com.gtastart.common.util.compose.widget.rememberComposeRecyclerViewState

@Composable
fun TestComposeRvScreen(
    modifier: Modifier = Modifier,
    viewModel: TestComposeRvViewModel = hiltViewModel()
) {
    val state = rememberComposeRecyclerViewState(false)
    val isRefreshing = rememberSaveable { mutableStateOf(false) }
    Column {
        Row {
            MButton(text = "添加数据", onClick = {
                viewModel._list.add(TestComposeRvBean(
                    id = viewModel._list.size + 1,
                    title = "标题",
                    content = "内容"
                ))
            })
            MButton(text = "删除数据", onClick = {
                Log.d("", "TestComposeRvScreen: ${viewModel._list.size}")
                viewModel._list.apply {
                    removeAt(this.size - 1)
                }
            })
            MButton(text = "更新数据", onClick = {
                viewModel.adapter.notifyDataSetChanged()
            })
        }
        ComposeRecyclerView(
            state = state,
            adapter = viewModel.adapter,
            isRefreshing = isRefreshing,
            onLoadMore = {},
            onLoadFailRetry = {},
            data = viewModel._list
        )
    }

    LaunchedEffect(Unit) {
        viewModel.listAdd(TestComposeRvBean(
            id = 1,
            title = "标题",
            content = "内容"
        ))
        viewModel.adapter.add(TestComposeRvBean(
            id = 1,
            title = "标题",
            content = "内容"
        ))
    }
}