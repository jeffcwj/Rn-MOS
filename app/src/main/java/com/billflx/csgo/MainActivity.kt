package com.billflx.csgo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.nav.RootNav
import com.billflx.csgo.page.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.MHelpers
import com.gtastart.common.util.MToast
import com.gtastart.data.constant.CsConstants
import com.valvesoftware.source.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.nillerusr.LauncherActivity
import javax.inject.Inject

val LocalMainViewModel = staticCompositionLocalOf<MainViewModel> {
    error("LocalMainViewModel Not Provide")
}

@AndroidEntryPoint
class MainActivity : LauncherActivity() {

    companion object {
        private const val TAG = "MainActivity"

        /**
         * 检测更新
         */
        suspend fun checkUpdate(mainViewModel: MainViewModel, context: Activity) {
            try {
                val notice = mainViewModel.getNotice()
                notice?.let {
                    Constants.appUpdateInfo.value = it // 存起来方便其他地方访问
                    CsConstants.csAppUpdateInfo.value = it
                    if (it.app.version != Constants.appVersion) { // 不是最新版本
                        val builder = MaterialAlertDialogBuilder(context)
                            .setTitle("${context.getString(R.string.has_new_version)} ${it.app.version}")
                            .setMessage(it.app.updateMsg)
                            .setPositiveButton(context.getString(R.string.update)
                            ) { dialog, which ->
                                MHelpers.openBrowser(context, it.app.link) // 访问浏览器更新软件
                            }
                            .setCancelable(true)
                        val versions = it.app.allowVersions.split(",")
                        if (versions.contains(Constants.appVersion)) {
                            val launch_screen_rootLayout = context.findViewById<RelativeLayout>(R.id.launch_screen_rootLayout)
                            launch_screen_rootLayout.setVisibility(View.GONE)
                            Log.d(TAG, "checkUpdate: hasUpdate!!!!!")
                            Constants.appUpdateInfo.value?.app?.hasUpdate = mutableStateOf(true)
                            builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                        } else {
                            Log.d(TAG, "checkUpdate: 版本过老")
                            builder.setNegativeButton(context.getString(R.string.cancel)) {dialog,_ -> dialog.dismiss() }
                        }
                        builder.show()
//                        launch_screen_refresh?.visibility = View.VISIBLE
                    } else {
                        Log.d(TAG, "checkUpdate: 已经是最新版本")
                        val launch_screen_rootLayout = context.findViewById<RelativeLayout>(R.id.launch_screen_rootLayout)
                        launch_screen_rootLayout.setVisibility(View.GONE)
                    }
                } ?: also {
                    Log.d(TAG, "checkUpdate: 检测更新失败")
                    withContext(Dispatchers.Main) {
                        Constants.isAppUpdateInfoFailed.value = true
//                        launch_screen_refresh?.visibility = View.VISIBLE
                    }
                }
            } catch (e: Throwable) { // 保底
                Log.d(TAG, "checkUpdate: 检测更新失败 $e")
                Constants.isAppUpdateInfoFailed.value = true
//                launch_screen_refresh?.visibility = View.VISIBLE
            }
        }
    }

//    private val settingViewModel: SettingViewModel by viewModels() // 这玩意跟compose里的实例还不一样，日了狗了
    private val mainViewModel: MainViewModel by viewModels()

    private var launch_screen_refresh: Button? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_compose)
        val composeView = findViewById<ComposeView>(R.id.composeView)
        composeView.setContent {

            GtaStartTheme(
                darkTheme = true
            ) {
                Surface {
                    CompositionLocalProvider(
                        LocalViewModelStoreOwner provides this,  // 同步作用域
                        LocalMainViewModel provides mainViewModel
                    ) {
                        RootNav()
                    }
                }
            }
        }


        // 添加启动加载界面
        val launch_app_screen = LayoutInflater.from(this).inflate(R.layout.launch_app_screen, null) as RelativeLayout
        addContentView(launch_app_screen, ViewGroup.LayoutParams(-1, -1))
        val launch_screen_rootLayout = findViewById<RelativeLayout>(R.id.launch_screen_rootLayout)
        launch_screen_rootLayout.setVisibility(View.GONE) // 不强制联网运行

        // 检测更新刷新按钮
        launch_screen_refresh = launch_app_screen.findViewById<Button>(R.id.launch_screen_refresh)
        launch_screen_refresh?.setOnClickListener {
            MToast("正在刷新...")
            lifecycleScope.launch {
                checkUpdate(mainViewModel, this@MainActivity)
            }
            launch_screen_refresh?.setVisibility(View.GONE)
        }

        // 检测更新
        lifecycleScope.launch {
            checkUpdate(mainViewModel, this@MainActivity)
        }
    }


    override fun onPause() {
        super.onPause()
    }

}