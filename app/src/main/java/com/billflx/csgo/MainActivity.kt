package com.billflx.csgo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import com.billflx.csgo.nav.RootNav
import com.billflx.csgo.page.SettingViewModel
import com.gtastart.common.base.BaseComposeActivity
import com.gtastart.common.theme.GtaStartTheme
import com.valvesoftware.source.R
import dagger.hilt.android.AndroidEntryPoint
import me.nillerusr.DirchActivity
import me.nillerusr.LauncherActivity

@AndroidEntryPoint
class MainActivity : LauncherActivity() {

//    private lateinit var selectPathResult: ActivityResultLauncher<Intent>
//    private val settingViewModel: SettingViewModel by viewModels() // 这玩意跟compose里的实例还不一样，日了狗了

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*selectPathResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                settingViewModel.handleActivityResult(data)
            }
        }*/

        setContentView(R.layout.layout_compose)
        val composeView = findViewById<ComposeView>(R.id.composeView)
        composeView.setContent {
            GtaStartTheme {
                Surface {
                    RootNav()
                }
            }
        }

/*        settingViewModel.openDirchActivityEvent.observe(this, Observer {
            Log.d("", "onCreate: 触发！")
            startDirchActivity(this)
        })*/
    }

/*    fun startDirchActivity(context: Context) {
        val intent = Intent(context, DirchActivity::class.java)
        selectPathResult.launch(intent)
    }*/

    override fun onPause() {
        super.onPause()
//        settingViewModel.saveSettings()
    }

}