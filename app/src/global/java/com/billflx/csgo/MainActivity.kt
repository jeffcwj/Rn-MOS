package com.billflx.csgo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.lifecycleScope
import com.billflx.csgo.bean.AppUpdateBean
import com.billflx.csgo.constant.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.gtastart.common.util.CommonUtils
import com.gtastart.common.util.MHelpers
import com.gtastart.common.util.MToast
import com.valvesoftware.source.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.nillerusr.LauncherActivity


@AndroidEntryPoint
class MainActivity : LauncherActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var launch_screen_refresh: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 添加启动加载界面
        val launch_app_screen = LayoutInflater.from(this).inflate(R.layout.launch_app_screen, null) as RelativeLayout
        addContentView(launch_app_screen, ViewGroup.LayoutParams(-1, -1))
        val launch_screen_rootLayout = findViewById<RelativeLayout>(R.id.launch_screen_rootLayout)
        launch_screen_rootLayout.setVisibility(View.GONE) // 不强制联网运行

        // 检测更新刷新按钮
        launch_screen_refresh = launch_app_screen.findViewById<Button>(R.id.launch_screen_refresh)
        launch_screen_refresh?.setOnClickListener {
            MToast.show(this, getString(R.string.refreshing))
            checkUpdate()
            launch_screen_refresh?.setVisibility(View.GONE)
        }

        initView()

        // 检测更新
        checkUpdate()
    }

    fun initView() {
        val btnDownloadRnCSFull = findViewById<Button>(R.id.button_download_rncs_full)
        btnDownloadRnCSFull.setOnClickListener {
            MHelpers.openBrowser(this, Constants.DownloadFullVerRnCSLink)
        }
        val btn_join_github = findViewById<Button>(R.id.btn_join_github)
        btn_join_github.setOnClickListener {
            MHelpers.openBrowser(this, "https://github.com/jeffcwj/RnCS")
        }
        val btn_join_rncs_qq = findViewById<Button>(R.id.btn_join_rncs_qq)
        btn_join_rncs_qq.setOnClickListener {
            MHelpers.joinQQGroup(key = "KHO17k0fUFnUlO73zH6L4kntKzGDJlZm", context = this)
        }
        val btn_join_cnsr_qq = findViewById<Button>(R.id.btn_join_cnsr_qq)
        btn_join_cnsr_qq.setOnClickListener {
            MHelpers.joinQQGroup(key = "t0N9hfPH9KMRKr-GmvIdUQC4bjL0nuM0", context = this)
        }
    }

    /**
     * 检测更新
     */
    fun checkUpdate() {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                Constants.CheckUpdateUrlList.forEach { updateUrl ->
                    CommonUtils.req(updateUrl,
                        onSuccess = { notice ->
                            lifecycleScope.launch {
                                val gson = Gson()
                                val it = gson.fromJson(notice, AppUpdateBean::class.java)
                                Constants.appUpdateInfo.value = it // 存起来方便其他地方访问
                                if (it.app.version != Constants.appVersion) { // 不是最新版本
                                    val builder = MaterialAlertDialogBuilder(this@MainActivity)
                                        .setTitle("${getString(R.string.has_new_version)} ${it.app.version}")
                                        .setMessage(it.app.updateMsg)
                                        .setPositiveButton(getString(R.string.update)
                                        ) { dialog, which ->
                                            MHelpers.openBrowser(this@MainActivity, it.app.link) // 访问浏览器更新软件
                                        }
                                        .setCancelable(true)
                                    val versions = it.app.allowVersions.split(",")
                                    if (versions.contains(Constants.appVersion)) {
                                        val launch_screen_rootLayout = findViewById<RelativeLayout>(R.id.launch_screen_rootLayout)
                                        launch_screen_rootLayout.setVisibility(View.GONE)
                                        Log.d(TAG, "checkUpdate: hasUpdate!!!!!")
                                        Constants.appUpdateInfo.value?.app?.hasUpdate = mutableStateOf(true)
                                        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                                    } else {
                                        Log.d(TAG, "checkUpdate: 版本过老")
                                        builder.setNegativeButton(getString(R.string.cancel)) {dialog,_ -> dialog.dismiss() }
                                    }
                                    builder.show()
                                    launch_screen_refresh?.visibility = View.VISIBLE
                                } else {
                                    Log.d(TAG, "checkUpdate: 已经是最新版本")
                                    val launch_screen_rootLayout = findViewById<RelativeLayout>(R.id.launch_screen_rootLayout)
                                    launch_screen_rootLayout.setVisibility(View.GONE)
                                }
                            }
                        },
                        onError = {
                            lifecycleScope.launch {
                                Log.d(TAG, "checkUpdate: 检测更新失败")
                                withContext(Dispatchers.Main) {
                                    launch_screen_refresh?.visibility = View.VISIBLE
                                }
                            }
                        })
                }
            }
        } catch (e: Throwable) { // 保底
            Log.d(TAG, "checkUpdate: 检测更新失败 $e")
            launch_screen_refresh?.visibility = View.VISIBLE
        }

    }


    override fun onPause() {
        super.onPause()
    }

}