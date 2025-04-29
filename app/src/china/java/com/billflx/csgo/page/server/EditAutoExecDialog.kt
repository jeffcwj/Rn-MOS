package com.billflx.csgo.page.server

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.billflx.csgo.bean.AutoExecCmdBean
import com.billflx.csgo.nav.LocalServerViewModel
import com.billflx.csgo.nav.LocalSettingViewModel
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.CSMOSUtils
import com.gtastart.common.util.MToast
import com.gtastart.common.util.compose.widget.MCustomAlertDialog
import com.gtastart.common.util.isBlank
import com.valvesoftware.source.R
import kotlinx.coroutines.launch

@Composable
fun EditAutoExecDialog(
    modifier: Modifier = Modifier,
    showDialog: MutableState<Boolean>
) {
    val context = LocalContext.current
    val showToturial = rememberSaveable { mutableStateOf(false) }
    val showCmds = rememberSaveable { mutableStateOf(false) }
    // 存储用户输入内容
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val settingVM = LocalSettingViewModel.current
    val viewModel = hiltViewModel<ServerViewModel>()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // val currentCSVersion = ModLocalDataSource.getCurrentCSVersion()
        // settingVM.applySettingsToModSP(currentCSVersion, true) // 临时切换路径
        textState.value = TextFieldValue(CSMOSUtils.removeAutoConnectInfo())
    }

    MCustomAlertDialog( // 设置启动命令弹窗
        title = stringResource(R.string.launch_cmd),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal),
                modifier = Modifier
            ) {
                DynamicHighlightedTextField(textState = textState)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
                ) {
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append(stringResource(R.string.show_toturial))
                        }
                    }, modifier = Modifier.clickable {
                        showToturial.value = !showToturial.value
                    })
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append(stringResource(R.string.view_common_cmds))
                        }
                    }, modifier = Modifier.clickable {
                        showCmds.value = !showCmds.value
                    })
                }

                when { // 教程区域
                    showToturial.value -> {
                        Text(stringResource(R.string.tip_one_line_one_cmd))
                        Box( // 演示边框
                            modifier = modifier
                                .border(
                                    width = 1.dp,
                                    color = colorResource(R.color.md_theme_primary),
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(GtaStartTheme.spacing.normal)
                        ) {
                            Text( // 演示文本
                                style = MaterialTheme.typography.bodySmall,
                                text = "sv_pure -1\npassword 123456"
                            )
                        }
                    }
                }

                when { // 查看常用命令弹窗
                    showCmds.value -> {
                        val viewModel = LocalServerViewModel.current
                        LaunchedEffect(Unit) {
                            viewModel.getAutoExecCmds()
                        }
                        MCustomAlertDialog(
                            title = stringResource(R.string.common_cmds),
                            content = {
                                if (viewModel.isAutoExecCmdLoading.value) {
                                    CircularProgressIndicator()
                                }
                                LazyColumn(
                                    modifier = Modifier
                                ) {
                                    items(viewModel.autoExecCmdList) { item ->
                                        ExecCmdsItem(
                                            item = item,
                                            textState = textState,
                                            showCmds = showCmds
                                        )
                                    }
                                }
                            },
                            onDismissRequest = {
                                showCmds.value = false
                            }
                        )
                    }
                }
            }
        },
        positiveButtonText = stringResource(R.string.save),
        onPositiveButtonClick = {
            scope.launch {
                // 保存
                // val currentCSVersion = ModLocalDataSource.getCurrentCSVersion()
                viewModel.applySettingsToModSpNew()
                CSMOSUtils.writeAutoExecText(textState.value.text)
                MToast.show(context, context.getString(R.string.save_finished))
                showDialog.value = false
            }
        },
        onDismissRequest = {
            showDialog.value = false
        }
    )
}

@Composable
private fun ExecCmdsItem(
    modifier: Modifier = Modifier,
    item: AutoExecCmdBean,
    textState: MutableState<TextFieldValue>,
    showCmds: MutableState<Boolean>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small),
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(GtaStartTheme.spacing.normal))
            .clickable {
                textState.value =
                    TextFieldValue("${textState.value.text.trimStart()}${if (!textState.value.text.isBlank()) "\n" else ""}${item.cmd}")
                showCmds.value = false
            }
            .padding(GtaStartTheme.spacing.normal)

    ) {
        Text(
            text = item.cmd?:"",
            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary)
        )
        Text(
            text = item.usage?:"",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DynamicHighlightedTextField(
    modifier: Modifier = Modifier,
    textState: MutableState<TextFieldValue>
) {
    // 样式：key 普通样式，value 高亮样式
    val keyStyle = SpanStyle(color = colorResource(R.color.md_theme_tertiary), fontSize = 16.sp) // MaterialTheme.colorScheme.onPrimary 为啥走的是LightTheme
    val valueStyle = SpanStyle(color = colorResource(R.color.md_theme_primary), fontSize = 16.sp)
    val scrollState = rememberScrollState()

//    TextField()
    Box( // 弄个假的文本外边框
        modifier = modifier
            .border(
                width = 1.dp,
                color = colorResource(R.color.md_theme_primary),
                shape = MaterialTheme.shapes.small // 圆角形状
            )
            .padding(GtaStartTheme.spacing.medium) // 内边距与边框之间的间距

    ) {
        BasicTextField(
            value = textState.value,
            onValueChange = { newValue ->
                textState.value = newValue
            },
            modifier = Modifier
                .verticalScroll(scrollState)
                .focusable(),
            cursorBrush = SolidColor(TextFieldDefaults.colors().cursorColor), // 光标颜色
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = Color.Transparent),
            decorationBox = { innerTextField ->
                // 创建高亮文本
                val annotatedText = buildAnnotatedString {
                    val lines = textState.value.text.split("\n") // 按行分割
                    lines.forEachIndexed { index, line ->
                        val parts = line.split(" ", limit = 2) // 按第一个空格分割成 key 和 value
                        val key = parts.getOrNull(0) ?: ""
                        val value = parts.getOrNull(1) ?: ""

                        // 为 key 部分添加样式
                        append(AnnotatedString(key, keyStyle))

                        // 为 value 部分添加样式
                        if (value.isNotEmpty()) {
                            append(" ")
                            append(AnnotatedString(value, valueStyle))
                        }

                        // 添加换行符（除最后一行）
                        if (index != lines.size - 1) {
                            append("\n")
                        }
                    }
                }

                // 显示带样式的内容
                BasicText(
                    text = annotatedText,
                    modifier = Modifier.fillMaxWidth(),
                    style = LocalTextStyle.current.copy(fontSize = 16.sp),
                )

                // 渲染原始文本编辑框（透明）以保持可编辑性
                innerTextField()
            }
        )
    }
}
