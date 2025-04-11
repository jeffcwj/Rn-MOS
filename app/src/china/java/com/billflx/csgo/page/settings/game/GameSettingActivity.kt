package com.billflx.csgo.page.settings.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.gtastart.common.theme.GtaStartTheme
import com.valvesoftware.source.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameSettingActivity : ComponentActivity() {

    // private val viewModel: GameSettingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GtaStartTheme {
                Surface {
                    val gameSettingViewModel = hiltViewModel<GameSettingViewModel>(
                        viewModelStoreOwner = LocalViewModelStoreOwner.current!!
                    )
                    GameSettings(
                        viewModel = gameSettingViewModel,
                        focusGameResPathItem = true
                    )
                }
            }
        }
    }
}