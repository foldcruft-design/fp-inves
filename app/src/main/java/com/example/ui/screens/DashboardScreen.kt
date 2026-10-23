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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.GoogleBlue
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
        mutableStateOf("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
    }

    val activeAccount = accounts.firstOrNull { it.isActive } ?: accounts.firstOrNull()
    val totalRaises = lotCategories.sumOf { it.totalRaisesCount }
    val unconfirmedOver24hCount = orders.count {
        it.status == "PAID" && !it.isSupportTicketSent &&
            (System.currentTimeMillis() - it.createdAtTimestamp) >= (settings.unconfirmedThresholdHours * 3600_000L)
    }

    val powerScale by animateFloatAsState(
        targetValue = if (settings.isSessionLoopRunning) 1.05f else 1f,
        label = "power_btn_scale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Neon Cyber Hero Status Card
        item {
            DarkNeonGlassCard(
                elevated = true,
                accentBorderColor = if (settings.isSessionLoopRunning) NeonEmerald else NeonCyan
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DarkNeonStatusBadge(
                                text = if (settings.isSessionLoopRunning) "СЕССИЯ 24/7 АКТИВНА" else "СЕССИЯ ОСТАНОВЛЕНА",
                                color = if (settings.isSessionLoopRunning) NeonEmerald else TextDim
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            DarkNeonStatusBadge(
                                text = "БЕЗ ЛИМИТОВ (PRO)",
                                color = NeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "FunPay Tools Pro",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Автоподнятие лотов • Автоответчик • Автотикеты в ТП (>24ч) • Сессии устройств",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextLight
                        )
                    }

                    // Futuristic Cyber Power Button
                    Box(
                        modifier = Modifier
                            .scale(powerScale)
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = if (settings.isSessionLoopRunning) {
                                        listOf(NeonEmerald, Color(0xFF047857))
                                    } else {
                                        listOf(NeonCyan, Color(0xFF0284C7))
                                    }
                                )
                            )
                            .clickable { onToggleSession24x7(!settings.isSessionLoopRunning) }
                            .testTag("toggle_session_24_7_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Питание сессии",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3 Cyber Metric Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DarkMetricTile(
                        title = "Баланс FunPay",
                        value = if (activeAccount != null) "${activeAccount.balanceRub.toInt()} ₽" else "15 800 ₽",
                        subtitle = activeAccount?.username ?: "Google Аккаунт",
                        accentColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    DarkMetricTile(
                        title = "Поднятий лотов",
                        value = "$totalRaises",
                        subtitle = "${lotCategories.count { it.isAutoRaiseEnabled }} категорий",
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

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Поднять лоты", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onScanOverdueOrdersNow,
                        enabled = !isBusy,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("quick_scan_tp_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NeonAmber)
                    ) {
                        Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Тикеты в ТП", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // 2. Google Registration & FunPay Login in App (WebView Interceptor)
        item {
            DarkNeonGlassCard(accentBorderColor = GoogleBlue) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GoogleLogoIcon(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Вход и Регистрация на FunPay через Google",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Переход на страницу регистрации FunPay с авто-захватом сессии",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = TextLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Primary Registration / Login Button via Webview
                Button(
                    onClick = {
                        // Open built-in browser directed to FunPay registration / Google login
                        onOpenFunPayWebAuth("https://funpay.com/account/login")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("google_login_webview_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = TextWhite
                    ),
                    border = BorderStroke(1.2.dp, GoogleBlue)
                ) {
                    GoogleLogoIcon(modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Войти / Зарегистрироваться на FunPay (через Google)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Direct Registration Link Button
                OutlinedButton(
                    onClick = {
                        onOpenFunPayWebAuth("https://funpay.com/account/register")
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, NeonCyan)
                ) {
                    Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Открыть форму регистрации funpay.com/account/register",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Existing Linked Accounts List
                if (accounts.isNotEmpty()) {
                    accounts.forEach { acc ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSwitchAccount(acc) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (acc.isActive) Color(0xFF0F2338) else Color(0xFF0A0F17),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (acc.isActive) NeonCyan else DarkBorder
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
                                        tint = if (acc.isActive) NeonEmerald else TextDim,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${acc.username} (ID: ${acc.userId})",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 14.sp,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "Баланс: ${acc.balanceRub.toInt()} ₽ • ${if (acc.isActive) "Активная сессия" else "Нажмите для выбора"}",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 11.sp,
                                            color = if (acc.isActive) NeonCyan else TextDim
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onDeleteAccount(acc) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Удалить",
                                        tint = NeonCoral
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (showManualKeyForm) "▲ Скрыть ручной ввод golden_key" else "▼ Или вставить ключ вручную / экспресс-вход",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextCyan,
                    modifier = Modifier.clickable { showManualKeyForm = !showManualKeyForm }
                )

                AnimatedVisibility(visible = showManualKeyForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = { usernameInput = it },
                            label = { Text("Ник на FunPay") },
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
                            label = { Text("golden_key (cookie)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onLoginGoogleFast("Google Trader", "trader@gmail.com") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, GoogleBlue)
                            ) {
                                Text("Экспресс-вход", fontSize = 12.sp, color = TextWhite)
                            }
                            Button(
                                onClick = {
                                    onConnectAccount(usernameInput, goldenKeyInput, userAgentInput)
                                    showManualKeyForm = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                            ) {
                                Text("Сохранить", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. Modules Shortcuts & Device Sessions Info
        item {
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Модули автоматизации (Без лимитов)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    DarkNeonStatusBadge(text = "UNLIMITED", color = NeonEmerald)
                }
                Spacer(modifier = Modifier.height(10.dp))

                DarkModuleRow(
                    icon = Icons.Default.Bolt,
                    title = "Автоподнятие лотов 24/7",
                    subtitle = "Активных категорий: ${lotCategories.count { it.isAutoRaiseEnabled }} • Без лимита подъемов",
                    checked = settings.isAutoRaiseMasterEnabled,
                    onClick = { onNavigateToTab(1) }
                )

                DarkModuleRow(
                    icon = Icons.Default.CheckCircle,
                    title = "Автоответчик и Приветствие",
                    subtitle = if (settings.isGreetingEnabled) "Приветствие включено" else "Только ключевые слова",
                    checked = settings.isGreetingEnabled,
                    onClick = { onNavigateToTab(2) }
                )

                DarkModuleRow(
                    icon = Icons.Default.SupportAgent,
                    title = "Отзывы и Автотикет в ТП (>24ч)",
                    subtitle = "Заказов без подтверждения >24ч: $unconfirmedOver24hCount • Без ограничений",
                    checked = settings.isAutoTicketTpEnabled,
                    onClick = { onNavigateToTab(3) }
                )

                DarkModuleRow(
                    icon = Icons.Default.NotificationsActive,
                    title = "Сессии устройств & Сервер 24/7 (Старый девайс)",
                    subtitle = "Настройка задач для каждого старого смартфона и Telegram мост",
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
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0C131D),
        border = BorderStroke(1.dp, Color(0x3338BDF8))
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
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF132235)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, fontSize = 14.sp, color = TextWhite)
                    Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = TextDim)
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = { onClick() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = NeonEmerald,
                    uncheckedThumbColor = TextDim,
                    uncheckedTrackColor = Color(0xFF1E293B)
                )
            )
        }
    }
}
