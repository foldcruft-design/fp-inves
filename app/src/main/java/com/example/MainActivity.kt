package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.GlassStatusBadge
import com.example.ui.components.LightGlassMeshBackground
import com.example.ui.screens.AutoReplyAndGreetingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LotsAutoRaiseScreen
import com.example.ui.screens.OrdersReviewsTicketsScreen
import com.example.ui.screens.TelegramAndServerScreen
import com.example.ui.theme.FPGlassHelperTheme
import com.example.ui.theme.GlassAccentMint
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.TextSlateDark
import com.example.ui.theme.TextSlateMuted
import com.example.viewmodel.FunPayViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FPGlassHelperTheme {
                FPGlassMainApp(
                    onUpdateKeepScreenOn = { keepOn ->
                        if (keepOn) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        }
                    }
                )
            }
        }
    }
}

private data class GlassNavItem(
    val index: Int,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun FPGlassMainApp(
    viewModel: FunPayViewModel = viewModel(),
    onUpdateKeepScreenOn: (Boolean) -> Unit = {}
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val lotCategories by viewModel.lotCategories.collectAsStateWithLifecycle()
    val autoReplyRules by viewModel.autoReplyRules.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBannerMessage.collectAsStateWithLifecycle()
    val isBusy by viewModel.isBusy.collectAsStateWithLifecycle()
    val simulatorReplies by viewModel.chatSimulatorReplies.collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // Keep screen awake when configured for old-device 24/7 server mode
    DisposableEffect(settings.isKeepScreenAwake) {
        onUpdateKeepScreenOn(settings.isKeepScreenAwake)
        onDispose { }
    }

    // Request POST_NOTIFICATIONS permission on Android 13+ when starting 24/7 foreground session
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )
    val context = LocalContext.current

    // Auto-dismiss banner after 4.5 seconds
    LaunchedEffect(statusBanner) {
        if (statusBanner != null) {
            delay(4500L)
            viewModel.dismissBanner()
        }
    }

    if (selectedTab != 0) {
        BackHandler {
            selectedTab = 0
        }
    }

    val navItems = listOf(
        GlassNavItem(
            index = 0,
            labelRes = R.string.nav_dashboard,
            selectedIcon = Icons.Filled.Dashboard,
            unselectedIcon = Icons.Outlined.Dashboard,
            testTag = "nav_tab_dashboard"
        ),
        GlassNavItem(
            index = 1,
            labelRes = R.string.nav_lots,
            selectedIcon = Icons.Filled.RocketLaunch,
            unselectedIcon = Icons.Outlined.RocketLaunch,
            testTag = "nav_tab_lots"
        ),
        GlassNavItem(
            index = 2,
            labelRes = R.string.nav_auto_reply,
            selectedIcon = Icons.Filled.Forum,
            unselectedIcon = Icons.Outlined.Forum,
            testTag = "nav_tab_auto_reply"
        ),
        GlassNavItem(
            index = 3,
            labelRes = R.string.nav_orders_tp,
            selectedIcon = Icons.Filled.ReceiptLong,
            unselectedIcon = Icons.Outlined.ReceiptLong,
            testTag = "nav_tab_orders_tp"
        ),
        GlassNavItem(
            index = 4,
            labelRes = R.string.nav_server,
            selectedIcon = Icons.Filled.Dns,
            unselectedIcon = Icons.Outlined.Dns,
            testTag = "nav_tab_server"
        )
    )

    LightGlassMeshBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xD9FFFFFF),
                    border = BorderStroke(1.dp, Color.White),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "FP GlassHelper",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextSlateDark
                            )
                            val activeAcc = accounts.firstOrNull { it.isActive } ?: accounts.firstOrNull()
                            Text(
                                text = if (activeAcc != null) {
                                    "Аккаунт: ${activeAcc.username} • ID ${activeAcc.userId}"
                                } else {
                                    "Автоматизация FunPay • Сессия 24/7"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 11.sp,
                                color = TextSlateMuted
                            )
                        }

                        GlassStatusBadge(
                            text = if (settings.isSessionLoopRunning) "ОНЛАЙН 24/7" else "ГОТОВ",
                            color = if (settings.isSessionLoopRunning) GlassAccentMint else GlassPrimary,
                            modifier = Modifier.clickable {
                                if (!settings.isSessionLoopRunning && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                                viewModel.toggle24x7SessionServer(!settings.isSessionLoopRunning)
                            }
                        )
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xEBFFFFFF),
                    border = BorderStroke(1.2.dp, Color.White),
                    shadowElevation = 8.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    ) {
                        navItems.forEach { item ->
                            val isSelected = selectedTab == item.index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = item.index },
                                modifier = Modifier.testTag(item.testTag),
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = stringResource(id = item.labelRes)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(id = item.labelRes),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = GlassPrimary,
                                    selectedTextColor = GlassPrimary,
                                    indicatorColor = Color(0xFFE0F2FE),
                                    unselectedIconColor = TextSlateMuted,
                                    unselectedTextColor = TextSlateMuted
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> DashboardScreen(
                        accounts = accounts,
                        lotCategories = lotCategories,
                        orders = orders,
                        settings = settings,
                        isBusy = isBusy,
                        onConnectAccount = { user, key, ua ->
                            viewModel.connectFunPayAccount(user, key, ua)
                        },
                        onSwitchAccount = { viewModel.switchActiveAccount(it) },
                        onDeleteAccount = { viewModel.deleteAccount(it) },
                        onToggleSession24x7 = { enable ->
                            if (enable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            viewModel.toggle24x7SessionServer(enable)
                        },
                        onRaiseAllNow = { viewModel.raiseAllCategoriesNow() },
                        onScanOverdueOrdersNow = { viewModel.runScanOverdueOrdersForTp() },
                        onNavigateToTab = { selectedTab = it }
                    )

                    1 -> LotsAutoRaiseScreen(
                        lotCategories = lotCategories,
                        settings = settings,
                        isBusy = isBusy,
                        onUpdateSettings = { viewModel.saveAutomationSettings(it) },
                        onAddCategory = { gId, nId, title, gName, count, interval ->
                            viewModel.addLotCategory(gId, nId, title, gName, count, interval)
                        },
                        onToggleCategory = { cat, en -> viewModel.toggleCategoryRaise(cat, en) },
                        onDeleteCategory = { viewModel.deleteCategory(it) },
                        onRaiseSingleNow = { viewModel.raiseSingleCategoryNow(it) },
                        onRaiseAllNow = { viewModel.raiseAllCategoriesNow() }
                    )

                    2 -> AutoReplyAndGreetingScreen(
                        settings = settings,
                        rules = autoReplyRules,
                        simulatorReplies = simulatorReplies,
                        onSaveSettings = { viewModel.saveAutomationSettings(it) },
                        onSaveRule = { viewModel.saveAutoReplyRule(it) },
                        onToggleRule = { rule, en -> viewModel.toggleAutoReplyRule(rule, en) },
                        onDeleteRule = { viewModel.deleteAutoReplyRule(it) },
                        onTestChatMessage = { buyer, msg, lot, isFirst ->
                            viewModel.testIncomingBuyerMessage(buyer, msg, lot, isFirst)
                        },
                        onPreviewTemplate = { viewModel.previewTemplate(it) }
                    )

                    3 -> OrdersReviewsTicketsScreen(
                        orders = orders,
                        settings = settings,
                        isBusy = isBusy,
                        onSaveSettings = { viewModel.saveAutomationSettings(it) },
                        onConfirmOrderAndReview = { viewModel.confirmOrderWithReviewAutomation(it) },
                        onSendTicketToTp = { viewModel.sendAutoTicketToSupportNow(it) },
                        onScanAllOverdueOrdersForTp = { viewModel.runScanOverdueOrdersForTp() },
                        onCreateTestOrder = { buyer, lot, price, hours ->
                            viewModel.createSampleOrder(buyer, lot, price, hours)
                        }
                    )

                    4 -> TelegramAndServerScreen(
                        settings = settings,
                        logs = logs,
                        isBusy = isBusy,
                        onSaveSettings = { viewModel.saveAutomationSettings(it) },
                        onToggleSession24x7 = { enable ->
                            if (enable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            viewModel.toggle24x7SessionServer(enable)
                        },
                        onTestTelegram = { token, chat ->
                            viewModel.testTelegramNotification(token, chat)
                        },
                        onClearLogs = { viewModel.clearLogs() }
                    )
                }

                // Floating Glass Notification Banner at the top of the screen
                AnimatedVisibility(
                    visible = statusBanner != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xF50F172A),
                        border = BorderStroke(1.dp, GlassPrimary),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = statusBanner.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.dismissBanner() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Закрыть",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
