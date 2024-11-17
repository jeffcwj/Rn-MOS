package com.billflx.csgo.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.compose.widget.MAlertDialog
import com.valvesoftware.source.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("CS:MOS", modifier = modifier.padding(start = GtaStartTheme.spacing.small))
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher),
                        contentDescription = null,
                        modifier = modifier.size(36.dp).clip(MaterialTheme.shapes.medium),
                    )
                }
            )
        },
    ) { innerPadding ->
        MainContent(
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(GtaStartTheme.spacing.medium)
    ) {
        NoticeCard()
    }
}

@Composable
private fun NoticeCard(
    modifier: Modifier = Modifier
) {
    var title by rememberSaveable { mutableStateOf("公告") }
    var content by rememberSaveable { mutableStateOf("加载中...") }
    val openDialog = rememberSaveable { mutableStateOf(false) }

    when {
        openDialog.value -> {
            MAlertDialog(
                title = title,
                content = content,
                positiveButtonText = "确定",
                onDismissRequest = {openDialog.value = false}
            )
        }
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
            .clickable {
                openDialog.value = true
            }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal),
            modifier = modifier.padding(GtaStartTheme.spacing.medium)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}