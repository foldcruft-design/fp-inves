package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutomationSettingsEntity
import com.example.data.FunPayAccountEntity
import com.example.data.FunPayOrderEntity
import com.example.data.LotCategoryEntity
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.GlassMetricTile
import com.example.ui.components.GlassStatusBadge
import com.example.ui.theme.GlassAccentDanger
import com.example.ui.theme.GlassAccentMint
import com.example.ui.theme.GlassAccentWarning
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.GlassSecondary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.TextSlateDark
import com.example.ui.theme.TextSlateMedium
import com.example.ui.theme.TextSlateMuted

@Composable
fun DashboardScreen(
    accounts: List<FunPayAccountEntity>,
    lotCategories: List<LotCategoryEntity>,
    orders: List<FunPayOrderEntity>,
    settings: AutomationSettingsEntity,
    isBusy: Boolean,
    onConnectAccount: (String, String, String) -> Unit,
    onSwitchAccount: (FunPayAccountEntity) -> Unit,
    onDeleteAccount: (FunPayAccountEntity) -> Unit,
    onToggleSession24x7: (Boolean) -> Unit,
    onRaiseAllNow: () -> Unit,
    onScanOverdueOrdersNow: () -> Unit,
    onNavigateToTab: (Int) -> Unit
) {
    var showAddAccountForm by remember { mutableStateOf(accounts.isEmpty()) }
    var usernameInput by remember { mutableStateOf("") }
    var goldenKeyInput by remember { mutableStateOf("") }
    var userAgentInput by remember {
        mutableStateOf("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/131.0.0.0 Safari/537.36")
    }
    var showCookieGuide by remember { mutableStateOf(false) }

    val activeAccount = accounts.firstOrNull { it.isActive } ?: accounts.firstOrNull()
    val totalRaises = lotCategories.sumOf { it.totalRaisesCount }
    val unconfirmedOver24hCount = orders.count {
        it.status == "PAID" && !it.isSupportTicketSent &&
            (System.currentTimeMillis() - it.createdAtTimestamp) >= (settings.unconfirmedThresholdHours * 3600_000L)
    }
    val totalTicketsSent = orders.count { it.isSupportTicketSent }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Crystal Glass Banner
        item {
            FrostedGlassCard(
                elevated = true,
                accentBorderColor = if (settings.isSessionLoopRunning) GlassAccentMint else GlassPrimary
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        GlassStatusBadge(
                            text = if (settings.isSessionLoopRunning) "СЕРВЕР-СЕССИЯ 24/7 АКТИВНА" else "СЕССИЯ В ОЖИДАНИИ",
                            color = if (settings.isSessionLoopRunning) GlassAccentMint else GlassPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "FP GlassHelper Pro",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextSlateDark
                        )
                        Text(
                            text = "Автоподнятие лотов • Автоответ • Отзывы • Автотикеты в ТП (>24ч) • Telegram",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSlateMedium
                        )
                    }

                    // Power Toggle Button for 24/7 Session
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = if (settings.isSessionLoopRunning) {
                                        listOf(GlassAccentMint, Color(0xFF059669))
                                    } else {
                                        listOf(GlassPrimary, GlassSecondary)
                                    }
                                )
                            )
                            .clickable { onToggleSession24x7(!settings.isSessionLoopRunning) }
                            .testTag("toggle_session_24_7_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Запустить сессию 24/7",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4 Live Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassMetricTile(
                        title = "Баланс FunPay",
                        value = if (activeAccount != null) "${activeAccount.balanceRub.toInt()} ₽" else "12 450 ₽",
                        subtitle = activeAccount?.username ?: "Демо-режим",
                        accentColor = GlassPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    GlassMetricTile(
                        title = "Поднятий в ТОП",
                        value = "$totalRaises",
                        subtitle = "${lotCategories.count { it.isAutoRaiseEnabled }} актив. разд.",
                        accentColor = GlassAccentMint,
                        modifier = Modifier.weight(1f)
                    )
                    GlassMetricTile(
                        title = "Заказы >24ч",
                        value = "$unconfirmedOver24hCount",
                        subtitle = "Отправлено ТП: $totalTicketsSent",
                        accentColor = if (unconfirmedOver24hCount > 0) GlassAccentWarning else GlassPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onRaiseAllNow,
                        enabled = !isBusy,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("quick_raise_all_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Поднять лоты", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onScanOverdueOrdersNow,
                        enabled = !isBusy,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("quick_scan_tp_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, GlassAccentWarning)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = GlassAccentWarning,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Тикеты в ТП (>24ч)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSlateDark
                        )
                    }
                }
            }
        }

        // 2. FunPay Account Connection & Multi-Account Manager
        item {
            FrostedGlassCard(accentBorderColor = GlassPrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = GlassPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Аккаунты FunPay (golden_key)",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSlateDark
                            )
                            Text(
                                text = "Привяжите аккаунт для поднятия лотов, чата и авто-тикетов",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = TextSlateMuted
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showAddAccountForm = !showAddAccountForm },
                        modifier = Modifier.testTag("toggle_add_account_button"),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить аккаунт",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showAddAccountForm) "Скрыть" else "Добавить", fontSize = 12.sp)
                    }
                }

                // Existing Linked Accounts
                if (accounts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    accounts.forEach { acc ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onSwitchAccount(acc) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (acc.isActive) Color(0xFFE0F2FE) else Color(0x99FFFFFF),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (acc.isActive) GlassPrimary else Color(0xFFE2E8F0)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (acc.isActive) Icons.Default.CheckCircle else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (acc.isActive) GlassAccentMint else TextSlateMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${acc.username} (ID: ${acc.userId})",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 14.sp,
                                            color = TextSlateDark
                                        )
                                        Text(
                                            text = "golden_key: ${acc.goldenKey.take(6)}••••••${acc.goldenKey.takeLast(4)} • Баланс: ${acc.balanceRub.toInt()} ₽",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 11.sp,
                                            color = TextSlateMuted
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onDeleteAccount(acc) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Удалить аккаунт",
                                        tint = GlassAccentDanger
                                    )
                                }
                            }
                        }
                    }
                }

                // Expandable Add Account Form
                AnimatedVisibility(visible = showAddAccountForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = { usernameInput = it },
                            label = { Text("Ваш ник на FunPay (или название профиля)") },
                            placeholder = { Text("Например: TopSeller_RU") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("account_username_input"),
                            shape = RoundedCornerShape(14.dp)
                        )

                        OutlinedTextField(
                            value = goldenKeyInput,
                            onValueChange = { goldenKeyInput = it },
                            label = { Text("Cookie golden_key (32 символа из браузера)") },
                            placeholder = { Text("Например: a8f9c2d1e4b709123456789abcdef012") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("account_golden_key_input"),
                            shape = RoundedCornerShape(14.dp)
                        )

                        OutlinedTextField(
                            value = userAgentInput,
                            onValueChange = { userAgentInput = it },
                            label = { Text("User-Agent (для обхода блокировок сессии)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Где взять golden_key на FunPay?",
                                style = MaterialTheme.typography.labelLarge,
                                color = GlassPrimary,
                                modifier = Modifier.clickable { showCookieGuide = !showCookieGuide }
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        usernameInput = "CrystalSeller_FP"
                                        goldenKeyInput = "fp98a7c6b5d4e3f2109876543210abcd"
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Демо-ключ", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        onConnectAccount(usernameInput, goldenKeyInput, userAgentInput)
                                        if (goldenKeyInput.trim().length >= 16) {
                                            showAddAccountForm = false
                                        }
                                    },
                                    enabled = !isBusy,
                                    modifier = Modifier.testTag("connect_account_submit_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                                ) {
                                    Text("Подключить аккаунт", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        AnimatedVisibility(visible = showCookieGuide) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.Info,
                                            contentDescription = null,
                                            tint = GlassPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Инструкция получения golden_key за 30 секунд:",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = TextSlateDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "1. Откройте сайт funpay.com в браузере (Chrome / Kiwi Browser / ПК) и войдите в аккаунт.\n" +
                                            "2. Нажмите F12 (DevTools) → вкладка Application → Cookies → https://funpay.com.\n" +
                                            "3. Скопируйте значение (Value) строки golden_key (32 символа) и вставьте выше.\n" +
                                            "4. Старый смартфон будет держать сессию онлайн 24/7 и поднимать лоты автоматически!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 12.sp,
                                        color = TextSlateMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Automation Modules Quick Overview Cards
        item {
            FrostedGlassCard {
                Text(
                    text = "Активные модули автоматизации",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSlateDark
                )
                Spacer(modifier = Modifier.height(10.dp))

                ModuleSummaryRow(
                    icon = Icons.Default.Bolt,
                    title = "Автоподнятие лотов и Вечный Онлайн",
                    subtitle = "Включено разделов: ${lotCategories.count { it.isAutoRaiseEnabled }} из ${lotCategories.size}",
                    checked = settings.isAutoRaiseMasterEnabled,
                    onClick = { onNavigateToTab(1) }
                )

                ModuleSummaryRow(
                    icon = Icons.Default.CheckCircle,
                    title = "Автоответчик и Приветствие новых покупателей",
                    subtitle = if (settings.isGreetingEnabled) "Приветствие активно + умные ключевые слова" else "Только ответы по ключевым словам",
                    checked = settings.isGreetingEnabled,
                    onClick = { onNavigateToTab(2) }
                )

                ModuleSummaryRow(
                    icon = Icons.Default.SupportAgent,
                    title = "Просьба отзыва, Автоответ на отзывы и Автотикет в ТП (>24ч)",
                    subtitle = "Неподтвержденных > ${settings.unconfirmedThresholdHours}ч: $unconfirmedOver24hCount шт. • Тикетов в ТП: $totalTicketsSent",
                    checked = settings.isAutoTicketTpEnabled,
                    onClick = { onNavigateToTab(3) }
                )

                ModuleSummaryRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "Telegram Уведомления + Сервер 24/7 (Старый девайс)",
                    subtitle = if (settings.isTelegramEnabled && settings.telegramBotToken.isNotBlank()) {
                        "Бот подключен (Chat ID: ${settings.telegramChatId})"
                    } else {
                        "Настройте Bot Token и Chat ID для мгновенных отчетов"
                    },
                    checked = settings.isTelegramEnabled,
                    onClick = { onNavigateToTab(4) }
                )
            }
        }
    }
}

@Composable
private fun ModuleSummaryRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xB3FFFFFF),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GlassPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        color = TextSlateDark
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = TextSlateMuted
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = { onClick() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = GlassAccentMint
                )
            )
        }
    }
}
