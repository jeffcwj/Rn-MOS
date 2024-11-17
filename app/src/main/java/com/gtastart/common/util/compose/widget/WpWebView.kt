package com.gtastart.common.util.compose.widget

import android.annotation.SuppressLint
import android.graphics.Color
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.appbar.AppBarLayout.LayoutParams

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WpWebView(
    modifier: Modifier = Modifier,
    url: String,
    javascript: (() -> String),
    onPageStarted: (webView: WebView) -> Unit = {},
    onPageFinished: (webView: WebView) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {

            }
        }
    ) { webView ->
        webView.settings.apply {
            javaScriptEnabled = true // 开启js支持
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK // 设置缓存优先，降低请求消耗
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // 注入 JavaScript 代码移除元素
                webView.evaluateJavascript(javascript.invoke(), null)
                onPageFinished.invoke(webView)
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                onPageStarted.invoke(webView)
            }
        }

        // 加载网页
        webView.loadUrl(url)

        // 设置背景颜色透明
        webView.setBackgroundColor(Color.TRANSPARENT)
    }
}