package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutomationSettingsEntity
import com.example.data.SessionLogEntity
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.GlassStatusBadge
import com.example.ui.theme.GlassAccentDanger
import com.example.ui.theme.GlassAccentMint
import com.example.ui.theme.GlassAccentWarning
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.OledBackground
import com.example.ui.theme.OledNeonCyan
import com.example.ui.theme.OledNeonGreen
import com.example.ui.theme.TextSlateDark
import com.example.ui.theme.TextSlateMedium
import com.example.ui.theme.TextSlateMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelegramAndServerScreen(
    settings: AutomationSettingsEntity,
    logs: List<SessionLogEntity>,
    isBusy: Boolean,
    onSaveSettings: (AutomationSettingsEntity) -> Unit,
    onToggleSession24x7: (Boolean) -> Unit,
    onTestTelegram: (String, String) -> Unit,
    onClearLogs: () -> Unit
) {
    var botTokenInput by remember(settings.telegramBotToken) {
        mutableStateOf(settings.telegramBotToken)
    }
    var chatIdInput by remember(settings.telegramChatId) {
        mutableStateOf(settings.telegramChatId)
    }
    var showOledFullscreenOverlay by remember { mutableStateOf(false) }

    val logTimeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    if (showOledFullscreenOverlay) {
        // Dedicated Old-Device 24/7 OLED Screen-Saver Server Mode
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OledBackground)
                .clickable { showOledFullscreenOverlay = false }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GlassStatusBadge(
                    text = "РЕЖИМ СТАРОГО СМАРТФОНА • СЕРВЕР 24/7 (АНТИ-ВЫГОРАНИЕ)",
                    color = OledNeonGreen
                )
                Text(
                    text = "FP GlassHelper Node",
                    style = MaterialTheme.typography.headlineLarge,
                    color = OledNeonCyan
                )
                Text(
                    text = if (settings.isSessionLoopRunning) {
                        "● Сессия FunPay активна • Поднятие лотов и мониторинг заказов >24ч работают"
                    } else {
                        "○ Фоновая сессия на паузе"
                    },
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 13.sp,
                    color = OledNeonGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Последние события сессии:",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = OledNeonCyan
                        )
                        logs.take(5).forEach { log ->
                            Text(
                                text = "[${logTimeFormat.format(Date(log.timestamp))}] ${log.title}",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Нажмите в любом месте экрана, чтобы вернуться в светлый стеклянный интерфейс",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("server_telegram_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Dedicated Old Device 24/7 Session Server Card
        item {
            FrostedGlassCard(elevated = true, accentBorderColor = GlassAccentMint) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = null,
                            tint = GlassPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            GlassStatusBadge(
                                text = "ДЛЯ СТАРОГО ДЕВАЙСА / СЕРВЕРА 24/7",
                                color = GlassAccentMint
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Режим постоянной сессии 24/7",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextSlateDark
                            )
                            Text(
                                text = "Положите старый телефон на зарядку — он будет держать Вечный Онлайн, поднимать лоты, отвечать в чате и писать в ТП по заказам >24ч",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = TextSlateMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Keep Screen Awake & Auto-Reconnect toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = GlassAccentMint,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Удерживать CPU и экран (WakeLock + Keep Screen On)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSlateDark
                        )
                    }
                    Switch(
                        checked = settings.isKeepScreenAwake,
                        onCheckedChange = {
                            onSaveSettings(settings.copy(isKeepScreenAwake = it))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GlassAccentMint
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onToggleSession24x7(!settings.isSessionLoopRunning) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("server_start_stop_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (settings.isSessionLoopRunning) GlassAccentDanger else GlassAccentMint
                        )
                    ) {
                        Icon(
                            imageVector = if (settings.isSessionLoopRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (settings.isSessionLoopRunning) "Остановить сессию" else "Запустить сервер 24/7",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { showOledFullscreenOverlay = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("oled_dim_mode_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, GlassPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brightness2,
                            contentDescription = null,
                            tint = GlassPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OLED Заставка 24/7",
                            fontSize = 12.sp,
                            color = GlassPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Real-Time Telegram Notifications Integration Card
        item {
            FrostedGlassCard(accentBorderColor = GlassPrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = GlassPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Интеграция с Telegram в реальном времени",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSlateDark
                            )
                            Text(
                                text = "Отслеживайте новые заказы, поднятие лотов, ответы в чате и тикеты в ТП прямо в Telegram",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = TextSlateMedium
                            )
                        }
                    }

                    Switch(
                        checked = settings.isTelegramEnabled,
                        onCheckedChange = {
                            onSaveSettings(
                                settings.copy(
                                    isTelegramEnabled = it,
                                    telegramBotToken = botTokenInput.trim(),
                                    telegramChatId = chatIdInput.trim()
                                )
                            )
                        },
                        modifier = Modifier.testTag("telegram_enable_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GlassAccentMint
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = botTokenInput,
                    onValueChange = { botTokenInput = it },
                    label = { Text("Telegram Bot Token (от @BotFather)") },
                    placeholder = { Text("Например: 7123456789:AAH_xxxxxxxxxxxxxxxxxxx") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("telegram_bot_token_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = chatIdInput,
                    onValueChange = { chatIdInput = it },
                    label = { Text("Ваш Telegram Chat ID (от @userinfobot)") },
                    placeholder = { Text("Например: 492817492") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("telegram_chat_id_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Какие уведомления присылать в Telegram:",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSlateDark
                )

                // Checkboxes for real-time events
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = settings.notifyOnLotRaise,
                            onCheckedChange = {
                                onSaveSettings(settings.copy(notifyOnLotRaise = it))
                            }
                        )
                        Text("Поднятие лотов", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = settings.notifyOnNewOrder,
                            onCheckedChange = {
                                onSaveSettings(settings.copy(notifyOnNewOrder = it))
                            }
                        )
                        Text("Новые заказы", fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = settings.notifyOnAutoTicketTp,
                            onCheckedChange = {
                                onSaveSettings(settings.copy(notifyOnAutoTicketTp = it))
                            }
                        )
                        Text("Автотикеты в ТП (>24ч)", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = settings.notifyOnOrderConfirmed,
                            onCheckedChange = {
                                onSaveSettings(settings.copy(notifyOnOrderConfirmed = it))
                            }
                        )
                        Text("Подтверждение и отзывы", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onSaveSettings(
                                settings.copy(
                                    isTelegramEnabled = true,
                                    telegramBotToken = botTokenInput.trim(),
                                    telegramChatId = chatIdInput.trim()
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GlassPrimary)
                    ) {
                        Text("Сохранить токен", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onTestTelegram(botTokenInput, chatIdInput) },
                        enabled = !isBusy,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_telegram_send_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Тест в Telegram", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Real-time Session Activity Log Console
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = GlassPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Журнал событий сессии (${logs.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSlateDark
                    )
                }

                IconButton(onClick = onClearLogs) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Очистить лог",
                        tint = TextSlateMuted
                    )
                }
            }
        }

        items(logs, key = { it.id }) { log ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xD9FFFFFF),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassStatusBadge(
                            text = log.category,
                            color = when (log.category) {
                                "TICKET_TP" -> GlassAccentWarning
                                "RAISE" -> GlassPrimary
                                "REVIEW" -> GlassAccentMint
                                "TELEGRAM" -> GlassPrimary
                                else -> if (log.isSuccess) GlassAccentMint else GlassAccentDanger
                            }
                        )
                        Text(
                            text = logTimeFormat.format(Date(log.timestamp)),
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = TextSlateMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = log.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        color = TextSlateDark
                    )
                    Text(
                        text = log.details,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = TextSlateMedium
                    )
                }
            }
        }
    }
}
