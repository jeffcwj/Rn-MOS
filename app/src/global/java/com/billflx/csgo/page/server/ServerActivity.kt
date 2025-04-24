package com.billflx.csgo.page.server

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.billflx.csgo.bean.SampQueryInfoBean
import com.billflx.csgo.data.ModLocalDataSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gtastart.common.util.CSMOSUtils
import com.gtastart.common.util.MToast
import com.valvesoftware.source.R
import com.valvesoftware.source.databinding.ActivityServerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.libsdl.app.SDLActivity

@AndroidEntryPoint
class ServerActivity : AppCompatActivity() {

    private val viewModel: ServerViewModel by viewModels()
    private lateinit var adapter: ServerAdapter
    private lateinit var binding: ActivityServerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupFab()
        observeViewModel()

        // 进入页面时自动刷新列表
        viewModel.refreshServerList()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        adapter = ServerAdapter { server ->
            showServerDetailDialog(server)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshServerList()
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            launchGame()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.serverInfoList.observe(this@ServerActivity) { servers ->
                    adapter.submitList(servers)
                }
                viewModel.isRefreshing.observe(this@ServerActivity) { isRefreshing ->
                    binding.swipeRefreshLayout.isRefreshing = isRefreshing
                }
            }
        }
    }

    private fun showServerDetailDialog(server: SampQueryInfoBean) {
        val editText = android.widget.EditText(this).apply {
            setText(ModLocalDataSource.getNickName().takeIf { it.isNotBlank() } ?: "RnCS Player")
            hint = getString(R.string.please_input_nickname)
        }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        // 添加服务器信息
        val serverInfo = android.widget.TextView(this).apply {
            text = """
                ${server.serverName}
                ${getString(R.string.map)}：${server.serverMap}
                ${getString(R.string.player_count)}：${server.playerCountInfo}
                ${getString(R.string.ping)}：${server.ping} ms
            """.trimIndent()
        }
        layout.addView(serverInfo)

        // 添加昵称输入框
        layout.addView(editText)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.detail)
            .setView(layout)
            .setPositiveButton(R.string.start_game) { _, _ ->
                val nickname = editText.text.toString()
                if (nickname.isBlank()) {
                    MToast.show(this, getString(R.string.nickname_cannot_empty))
                    return@setPositiveButton
                }
                viewModel.setNickName(nickname)
                ModLocalDataSource.setNickName(nickname) // 保存到 ModLocalDataSource
                launchGameWithServer(server, nickname)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun launchGame() {
        showNicknameDialog { nickname ->
            CSMOSUtils.saveNickName(nickname)
            ModLocalDataSource.setNickName(nickname) // 保存到 ModLocalDataSource
            CSMOSUtils.removeAutoConnectInfo() // 移除自动连接信息
            CSMOSUtils.addCustomMainServers() // 添加主服

            val intent = Intent(this, SDLActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_GAME)
        }
    }

    private fun launchGameWithServer(server: SampQueryInfoBean, nickname: String) {
        CSMOSUtils.saveNickName(nickname)
        CSMOSUtils.saveAutoConnectInfo(server.serverIP ?: "")
        CSMOSUtils.addCustomMainServers()

        val intent = Intent(this, SDLActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_GAME)
    }

    private fun showNicknameDialog(onConfirm: (String) -> Unit) {
        val editText = android.widget.EditText(this).apply {
            setText(ModLocalDataSource.getNickName().takeIf { it.isNotBlank() } ?: "RnCS Player")
            hint = getString(R.string.please_input_nickname)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.please_input_nickname)
            .setView(editText)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val nickname = editText.text.toString()
                if (nickname.isBlank()) {
                    MToast.show(this, getString(R.string.nickname_cannot_empty))
                    return@setPositiveButton
                }
                viewModel.setNickName(nickname)
                onConfirm(nickname)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GAME) {
            viewModel.refreshServerList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_server, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_settings -> {
                // TODO: 打开设置页面
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val REQUEST_CODE_GAME = 1001
    }
} 