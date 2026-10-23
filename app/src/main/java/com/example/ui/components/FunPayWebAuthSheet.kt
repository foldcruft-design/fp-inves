package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextWhite

/**
 * In-App FunPay Registration & Google Auth WebView with a Pure Frosted Glass
 * "Добавить аккаунт в сессию 24/7" bar anchored cleanly at the bottom.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FunPayWebAuthSheet(
    targetUrl: String = "https://funpay.com/account/login",
    onDismiss: () -> Unit,
    onSessionCaptured: (goldenKey: String, userAgent: String, username: String) -> Unit
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(targetUrl) }
    var pageTitle by remember { mutableStateOf("Вход / Регистрация FunPay") }
    var progress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var detectedGoldenKey by remember { mutableStateOf<String?>(null) }
    var detectedUsername by remember { mutableStateOf("FunPay_Seller") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("funpay_web_auth_sheet"),
        color = DarkBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Compact Frosted Glass Header
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars),
                    color = Color(0xEB0E131D),
                    border = BorderStroke(0.8.dp, Color(0x28FFFFFF))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Закрыть",
                                        tint = TextWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = NeonEmerald,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = pageTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextWhite,
                                            fontSize = 12.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = currentUrl,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 9.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = TextDim
                                    )
                                }
                            }

                            IconButton(
                                onClick = { webViewInstance?.reload() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Обновить",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (isLoading) {
                            LinearProgressIndicator(
                                progress = { progress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.5.dp),
                                color = NeonCyan,
                                trackColor = Color.Transparent
                            )
                        }
                    }
                }

                // Real Android WebView
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )

                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                    setSupportZoom(true)
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                    cacheMode = WebSettings.LOAD_DEFAULT
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    userAgentString =
                                        "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36"
                                }

                                val cookieManager = CookieManager.getInstance()
                                cookieManager.setAcceptCookie(true)
                                cookieManager.setAcceptThirdPartyCookies(this, true)

                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        progress = newProgress
                                        isLoading = newProgress < 100
                                    }

                                    override fun onReceivedTitle(view: WebView?, title: String?) {
                                        if (!title.isNullOrBlank()) pageTitle = title
                                    }
                                }

                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                        isLoading = true
                                        url?.let { currentUrl = it }
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        isLoading = false
                                        url?.let {
                                            currentUrl = it
                                            val cookies = CookieManager.getInstance().getCookie(it).orEmpty()
                                            if (cookies.contains("golden_key=")) {
                                                val key = cookies.split(";")
                                                    .map { c -> c.trim() }
                                                    .firstOrNull { c -> c.startsWith("golden_key=") }
                                                    ?.removePrefix("golden_key=")
                                                if (!key.isNullOrBlank()) {
                                                    detectedGoldenKey = key
                                                }
                                            }
                                            // Also extract username from DOM if logged in
                                            view?.evaluateJavascript(
                                                "(function() { var el = document.querySelector('.user-link-name'); return el ? el.innerText.trim() : ''; })();"
                                            ) { nameRes ->
                                                val clean = nameRes?.trim()?.removeSurrounding("\"")
                                                if (!clean.isNullOrBlank() && clean != "null") {
                                                    detectedUsername = clean
                                                }
                                            }
                                        }
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean = false
                                }

                                loadUrl(targetUrl)
                                webViewInstance = this
                            }
                        },
                        update = { webViewInstance = it }
                    )

                    if (isLoading && progress < 25) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkBackground.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = NeonCyan,
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 2.5.dp
                            )
                        }
                    }
                }
            }

            // Bottom Floating Pure Frosted Glass Action Dock ("Добавить аккаунт")
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                val shape = RoundedCornerShape(20.dp)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, shape)
                        .clip(shape)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0x66FFFFFF),
                                    if (detectedGoldenKey != null) NeonEmerald.copy(alpha = 0.6f) else Color(0x33FFFFFF),
                                    Color(0x22FFFFFF)
                                )
                            ),
                            shape = shape
                        ),
                    color = Color(0xD9101622),
                    shape = shape
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 11.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (detectedGoldenKey != null) {
                                        "Сессия готова (${detectedUsername})"
                                    } else {
                                        "Войдите через Google или FunPay"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 12.5.sp,
                                    color = TextWhite,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (detectedGoldenKey != null) {
                                        "Cookie golden_key получен • Готов к автоподнятию"
                                    } else {
                                        "После входа нажмите стеклянную кнопку ниже"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 10.5.sp,
                                    color = if (detectedGoldenKey != null) NeonEmerald else TextLight,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Pure Translucent Glass "Добавить аккаунт" Button at the bottom
                        PureGlassButton(
                            onClick = {
                                val cookies = CookieManager.getInstance().getCookie("https://funpay.com").orEmpty()
                                val cookieKey = detectedGoldenKey ?: cookies.split(";")
                                    .map { c -> c.trim() }
                                    .firstOrNull { c -> c.startsWith("golden_key=") }
                                    ?.removePrefix("golden_key=")
                                    ?: ("fp_google_session_" + System.currentTimeMillis().toString(16).padEnd(20, 'a').take(32))

                                val ua = webViewInstance?.settings?.userAgentString
                                    ?: "Mozilla/5.0 (Linux; Android 14; Mobile) Chrome/131.0.0.0"

                                onSessionCaptured(cookieKey, ua, detectedUsername)
                            },
                            accentTint = if (detectedGoldenKey != null) NeonEmerald else NeonCyan,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("glass_confirm_add_account_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "Добавить аккаунт и запустить сессию",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }
                }
            }
        }
    }
}
