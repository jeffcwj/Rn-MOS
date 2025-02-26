package com.billflx.csgo.page.test

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.compose.widget.MComposeListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class TestComposeRvBean(
    val id: Int,
    val title: String,
    val content: String
)

@HiltViewModel
class TestComposeRvViewModel @Inject constructor(
    private val app: Application
): ViewModel() {

    var _list = mutableStateListOf<TestComposeRvBean>()
    val list: List<TestComposeRvBean> get()  = _list.toList()
    val adapter = MComposeListAdapter<TestComposeRvBean>(_list) { it,pos ->
        val item = if (pos >= _list.size) it else _list[pos]
        Row(
            horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(GtaStartTheme.spacing.normal)
        ) {
            Text(item?.id?.toString()?:"0", style = MaterialTheme.typography.titleLarge)
            Column(
                verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
            ) {
                Text(item?.title.orEmpty(), style = MaterialTheme.typography.titleMedium)
                Text(item?.content.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    fun listAdd(element: TestComposeRvBean) {
        _list.add(
            TestComposeRvBean(
                id = 1,
                title = "adapter.items",
                content = "内容"
            )
        )
        /*adapter.mList.add(
            TestComposeRvBean(
                id = 1,
                title = "adapter.items",
                content = "内容"
            )
        )*/
        adapter.notifyDataSetChanged()
    }

    init {
        listAdd(TestComposeRvBean(
            id = 1,
            title = "标题",
            content = "内容"
        ))
        adapter.add(
            TestComposeRvBean(
                id = 2,
                title = "ViewModel init 代码块",
                content = "内容"
            )
        )
    }
}