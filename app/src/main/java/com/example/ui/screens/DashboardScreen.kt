package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutomationSettingsEntity
import com.example.data.FunPayAccountEntity
import com.example.data.FunPayOrderEntity
import com.example.data.LotCategoryEntity
import com.example.ui.components.DarkMetricTile
import com.example.ui.components.DarkNeonGlassCard
import com.example.ui.components.DarkNeonStatusBadge
import com.example.ui.components.GoogleLogoIcon
import com.example.ui.components.PureGlassButton
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextWhite

@Composable
fun DashboardScreen(
    accounts: List<FunPayAccountEntity>,
    lotCategories: List<LotCategoryEntity>,
    orders: List<FunPayOrderEntity>,
    settings: AutomationSettingsEntity,
    isBusy: Boolean,
    onOpenFunPayWebAuth: (targetUrl: String) -> Unit,
    onLoginGoogleFast: (String, String) -> Unit,
    onConnectAccount: (String, String, String) -> Unit,
    onSwitchAccount: (FunPayAccountEntity) -> Unit,
    onDeleteAccount: (FunPayAccountEntity) -> Unit,
    onToggleSession24x7: (Boolean) -> Unit,
    onRaiseAllNow: () -> Unit,
    onScanOverdueOrdersNow: () -> Unit,
    onNavigateToTab: (Int) -> Unit
) {
    var showManualKeyForm by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf("") }
    var goldenKeyInput by remember { mutableStateOf("") }
    var userAgentInput by remember {
        mutableStateOf("Mozilla/5.0 (Linux; Android 14; Mobile) Chrome/131.0.0.0")
    }

    val activeAccount = accounts.firstOrNull { it.isActive } ?: accounts.firstOrNull()
    val totalRaises = lotCategories.sumOf { it.totalRaisesCount }
    val unconfirmedOver24hCount = orders.count {
        it.status == "PAID" && !it.isSupportTicketSent &&
            (System.currentTimeMillis() - it.createdAtTimestamp) >= (settings.unconfirmedThresholdHours * 3600_000L)
    }

    val powerScale by animateFloatAsState(
        targetValue = if (settings.isSessionLoopRunning) 1.04f else 1f,
        label = "power_btn_scale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_list"),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        // 1. Sleek Dark Frosted Glass Hero Status Card
        item {
            DarkNeonGlassCard(
                elevated = true,
                accentBorderColor = if (settings.isSessionLoopRunning) NeonEmerald else null
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DarkNeonStatusBadge(
                                text = if (settings.isSessionLoopRunning) "СЕССИЯ 24/7 АКТИВНА" else "СЕССИЯ ГОТОВА",
                                color = if (settings.isSessionLoopRunning) NeonEmerald else NeonCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            DarkNeonStatusBadge(
                                text = "БЕЗ ЛИМИТОВ",
                                color = TextLight
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "FP GlassHelper",
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextWhite
                        )
                        Text(
                            text = "Автоподъем лотов • Автоответ • Автотикеты в ТП (>24ч)",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = TextLight
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Compact Glass Power Button
                    Box(
                        modifier = Modifier
                            .scale(powerScale)
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = if (settings.isSessionLoopRunning) {
                                        listOf(Color(0x3834D399), Color(0x1A34D399))
                                    } else {
                                        listOf(Color(0x2EFFFFFF), Color(0x14FFFFFF))
                                    }
                                )
                            )
                            .clickable { onToggleSession24x7(!settings.isSessionLoopRunning) }
                            .testTag("toggle_session_24_7_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Запуск сессии 24/7",
                            tint = if (settings.isSessionLoopRunning) NeonEmerald else TextWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(11.dp))

                // 3 Compact Frosted Metric Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DarkMetricTile(
                        title = "Баланс",
                        value = if (activeAccount != null) "${activeAccount.balanceRub.toInt()} ₽" else "15 800 ₽",
                        subtitle = activeAccount?.username ?: "Сессия",
                        accentColor = TextWhite,
                        modifier = Modifier.weight(1f)
                    )
                    DarkMetricTile(
                        title = "Поднятий",
                        value = "$totalRaises",
                        subtitle = "${lotCategories.count { it.isAutoRaiseEnabled }} разд.",
                        accentColor = NeonEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    DarkMetricTile(
                        title = "Заказы >24ч",
                        value = "$unconfirmedOver24hCount шт.",
                        subtitle = "В ТП: ${orders.count { it.isSupportTicketSent }}",
                        accentColor = if (unconfirmedOver24hCount > 0) NeonAmber else NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2 Pure Glass Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PureGlassButton(
                        onClick = onRaiseAllNow,
                        enabled = !isBusy,
                        accentTint = NeonCyan,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_raise_all_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Поднять лоты",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            color = TextWhite
                        )
                    }

                    PureGlassButton(
                        onClick = onScanOverdueOrdersNow,
                        enabled = !isBusy,
                        accentTint = NeonAmber,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_scan_tp_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Тикеты в ТП",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            color = TextWhite
                        )
                    }
                }
            }
        }

        // 2. Account & Session Card with Pure Glass "Добавить аккаунт" Button at the bottom
        item {
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GoogleLogoIcon()
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Аккаунт и Сессия FunPay",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite
                            )
                            Text(
                                text = "Вход через Google / сайт FunPay с авто-запуском сессии",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = TextLight
                            )
                        }
                    }
                }

                // Existing Linked Accounts
                if (accounts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(9.dp))
                    accounts.forEach { acc ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSwitchAccount(acc) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (acc.isActive) Color(0x2238BDF8) else Color(0x14FFFFFF),
                            border = BorderStroke(
                                width = 0.8.dp,
                                color = if (acc.isActive) NeonCyan.copy(alpha = 0.5f) else DarkBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 11.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (acc.isActive) Icons.Default.CheckCircle else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (acc.isActive) NeonEmerald else TextDim,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${acc.username} (ID: ${acc.userId})",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 12.5.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "${acc.balanceRub.toInt()} ₽ • ${if (acc.isActive) "Сессия активна (Онлайн + Лоты)" else "Нажмите для выбора"}",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = if (acc.isActive) TextCyan else TextDim
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onDeleteAccount(acc) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Удалить",
                                        tint = NeonCoral,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Row: Open FunPay Register or Quick Google Demo Session
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PureGlassButton(
                        onClick = { onOpenFunPayWebAuth("https://funpay.com/account/register") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = TextLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Регистрация",
                            fontSize = 11.sp,
                            maxLines = 1,
                            color = TextLight
                        )
                    }

                    PureGlassButton(
                        onClick = { onLoginGoogleFast("FunPay_Pro_Seller", "seller@gmail.com") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("google_login_one_click_button")
                    ) {
                        GoogleLogoIcon(modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Google Сессия 1-клик",
                            fontSize = 11.sp,
                            maxLines = 1,
                            color = TextWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pure Frosted Glass Button at the bottom of the card: "Добавить аккаунт"
                PureGlassButton(
                    onClick = {
                        onOpenFunPayWebAuth("https://funpay.com/account/login")
                    },
                    accentTint = NeonCyan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("google_login_webview_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "Добавить аккаунт (Google / FunPay)",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (showManualKeyForm) "▲ Скрыть ввод golden_key" else "▼ Ввести golden_key вручную",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 10.5.sp,
                    color = TextDim,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { showManualKeyForm = !showManualKeyForm }
                )

                AnimatedVisibility(visible = showManualKeyForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = { usernameInput = it },
                            label = { Text("Ник на FunPay", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = goldenKeyInput,
                            onValueChange = { goldenKeyInput = it },
                            label = { Text("Cookie golden_key", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        PureGlassButton(
                            onClick = {
                                onConnectAccount(usernameInput, goldenKeyInput, userAgentInput)
                                showManualKeyForm = false
                            },
                            accentTint = NeonEmerald,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Сохранить и запустить сессию",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                        }
                    }
                }
            }
        }

        // 3. Compact Glass Automation Modules Card
        item {
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Активные действия сессии",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite
                    )
                    DarkNeonStatusBadge(text = "АВТОМАТИКА", color = NeonEmerald)
                }
                Spacer(modifier = Modifier.height(8.dp))

                DarkModuleRow(
                    icon = Icons.Default.Bolt,
                    title = "Автоподнятие лотов 24/7",
                    subtitle = "Разделов: ${lotCategories.count { it.isAutoRaiseEnabled }} • Вечный онлайн",
                    checked = settings.isAutoRaiseMasterEnabled,
                    onClick = { onNavigateToTab(1) }
                )

                DarkModuleRow(
                    icon = Icons.Default.CheckCircle,
                    title = "Автоответ и Приветствие",
                    subtitle = if (settings.isGreetingEnabled) "Приветствие + ответы по триггерам" else "Ответы по триггерам",
                    checked = settings.isGreetingEnabled,
                    onClick = { onNavigateToTab(2) }
                )

                DarkModuleRow(
                    icon = Icons.Default.SupportAgent,
                    title = "Отзывы и Автотикет в ТП (>24ч)",
                    subtitle = "Просрочено >24ч: $unconfirmedOver24hCount шт. • Запрос отзыва",
                    checked = settings.isAutoTicketTpEnabled,
                    onClick = { onNavigateToTab(3) }
                )

                DarkModuleRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "Устройства 24/7 и Telegram",
                    subtitle = if (settings.isTelegramEnabled) "Уведомления в Telegram включены" else "Настройка старого телефона и бота",
                    checked = settings.isSessionLoopRunning,
                    onClick = { onNavigateToTab(4) }
                )
            }
        }
    }
}

@Composable
private fun DarkModuleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0x14FFFFFF),
        border = BorderStroke(0.8.dp, Color(0x22FFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 11.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color(0x1FFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 12.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = TextWhite
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = TextDim
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Switch(
                checked = checked,
                onCheckedChange = { onClick() },
                modifier = Modifier.scale(0.82f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NeonEmerald.copy(alpha = 0.75f),
                    uncheckedThumbColor = TextDim,
                    uncheckedTrackColor = Color(0x33FFFFFF)
                )
            )
        }
    }
}
