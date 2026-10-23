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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutomationSettingsEntity
import com.example.data.DeviceNodeSessionEntity
import com.example.data.SessionLogEntity
import com.example.ui.components.DarkNeonGlassCard
import com.example.ui.components.DarkNeonStatusBadge
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelegramAndServerScreen(
    settings: AutomationSettingsEntity,
    deviceNodes: List<DeviceNodeSessionEntity>,
    logs: List<SessionLogEntity>,
    isBusy: Boolean,
    onSaveSettings: (AutomationSettingsEntity) -> Unit,
    onToggleSession24x7: (Boolean) -> Unit,
    onAddDeviceNode: (name: String, role: String) -> Unit,
    onUpdateDeviceNode: (DeviceNodeSessionEntity) -> Unit,
    onDeleteDeviceNode: (DeviceNodeSessionEntity) -> Unit,
    onTestTelegram: (String, String) -> Unit,
    onClearLogs: () -> Unit
) {
    var botTokenInput by remember(settings.telegramBotToken) { mutableStateOf(settings.telegramBotToken) }
    var chatIdInput by remember(settings.telegramChatId) { mutableStateOf(settings.telegramChatId) }
    var showOledFullscreenOverlay by remember { mutableStateOf(false) }

    var showAddDeviceDialog by remember { mutableStateOf(false) }
    var newDeviceName by remember { mutableStateOf("") }
    var newDeviceRole by remember { mutableStateOf("") }

    val logTimeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    if (showOledFullscreenOverlay) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { showOledFullscreenOverlay = false }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DarkNeonStatusBadge(text = "РЕЖИМ СТАРОГО ТЕЛЕФОНА 24/7 (АНТИ-ВЫГОРАНИЕ)", color = NeonEmerald)
                Text(text = "FP Cyber Server Node", style = MaterialTheme.typography.headlineLarge, color = NeonCyan)
                Text(
                    text = if (settings.isSessionLoopRunning) "● Сессия активна • Поднятие лотов и тикеты в ТП работают" else "○ Сессия на паузе",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 13.sp,
                    color = NeonEmerald
                )
                Text(text = "Нажмите в любом месте экрана для выхода", style = MaterialTheme.typography.bodyMedium, color = TextDim)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("server_telegram_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Dedicated Old Device 24/7 Server Master Card
        item {
            DarkNeonGlassCard(elevated = true, accentBorderColor = NeonEmerald) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DarkNeonStatusBadge(text = "РЕЖИМ СЕРВЕРА 24/7 (СТАРЫЙ ДЕВАЙС)", color = NeonEmerald)
                                Spacer(modifier = Modifier.width(6.dp))
                                DarkNeonStatusBadge(text = "БЕЗ ЛИМИТОВ", color = NeonCyan)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Круглосуточный фоновый сервер", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(text = "Старый девайс на зарядке поднимает лоты, отвечает в чате и пишет в ТП", style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = TextLight)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Удерживать CPU и экран (WakeLock)", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                    Switch(
                        checked = settings.isKeepScreenAwake,
                        onCheckedChange = { onSaveSettings(settings.copy(isKeepScreenAwake = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonEmerald)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onToggleSession24x7(!settings.isSessionLoopRunning) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (settings.isSessionLoopRunning) NeonCoral else NeonEmerald,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(imageVector = if (settings.isSessionLoopRunning) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (settings.isSessionLoopRunning) "Остановить" else "Запустить 24/7", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showOledFullscreenOverlay = true },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Icon(imageVector = Icons.Default.Brightness2, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("OLED Заставка", color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Multi-Device Sessions Manager (Custom task assignment per device without limits)
        item {
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Сессии и Устройства (${deviceNodes.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Настройте, какие задачи выполняет это устройство",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp,
                            color = TextLight
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddDeviceDialog = !showAddDeviceDialog },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Девайс", color = NeonCyan, fontSize = 12.sp)
                    }
                }

                AnimatedVisibility(visible = showAddDeviceDialog) {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newDeviceName,
                            onValueChange = { newDeviceName = it },
                            label = { Text("Название (напр.: Старый Samsung A50)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = newDeviceRole,
                            onValueChange = { newDeviceRole = it },
                            label = { Text("Роль (напр.: Поднятие лотов 24/7)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        Button(
                            onClick = {
                                if (newDeviceName.isNotBlank()) {
                                    onAddDeviceNode(newDeviceName, newDeviceRole)
                                    newDeviceName = ""
                                    newDeviceRole = ""
                                    showAddDeviceDialog = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                        ) {
                            Text("Добавить устройство", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Device Nodes Items with granular tasks toggles
        items(deviceNodes, key = { it.id }) { node ->
            DarkNeonGlassCard(accentBorderColor = if (node.isSessionActive) NeonCyan else null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = node.deviceName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Text(text = node.deviceRole, style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = TextLight)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = node.isSessionActive,
                            onCheckedChange = { onUpdateDeviceNode(node.copy(isSessionActive = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonEmerald)
                        )
                        IconButton(onClick = { onDeleteDeviceNode(node) }) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, tint = NeonCoral)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Задачи этого устройства (без лимитов):",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextCyan
                )

                // Granular Tasks Checkboxes for this device
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = node.taskAutoRaiseLots,
                            onCheckedChange = { onUpdateDeviceNode(node.copy(taskAutoRaiseLots = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                        )
                        Text(text = "Подъем лотов", fontSize = 12.sp, color = TextLight)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = node.taskAutoReplyChat,
                            onCheckedChange = { onUpdateDeviceNode(node.copy(taskAutoReplyChat = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                        )
                        Text(text = "Автоответчик", fontSize = 12.sp, color = TextLight)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = node.taskAutoTpTickets,
                            onCheckedChange = { onUpdateDeviceNode(node.copy(taskAutoTpTickets = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = NeonAmber)
                        )
                        Text(text = "Тикеты в ТП (>24ч)", fontSize = 12.sp, color = TextLight)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = node.taskAutoReviews,
                            onCheckedChange = { onUpdateDeviceNode(node.copy(taskAutoReviews = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = NeonEmerald)
                        )
                        Text(text = "Отзывы", fontSize = 12.sp, color = TextLight)
                    }
                }
            }
        }

        // 3. Telegram Integration Card
        item {
            DarkNeonGlassCard(accentBorderColor = NeonCyan) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Telegram Уведомления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text(text = "Мгновенные оповещения в Telegram бота", style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = TextLight)
                    }
                    Switch(
                        checked = settings.isTelegramEnabled,
                        onCheckedChange = {
                            onSaveSettings(settings.copy(isTelegramEnabled = it, telegramBotToken = botTokenInput.trim(), telegramChatId = chatIdInput.trim()))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonCyan)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = botTokenInput,
                    onValueChange = { botTokenInput = it },
                    label = { Text("Bot Token (@BotFather)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = chatIdInput,
                    onValueChange = { chatIdInput = it },
                    label = { Text("Chat ID (@userinfobot)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onSaveSettings(settings.copy(isTelegramEnabled = true, telegramBotToken = botTokenInput.trim(), telegramChatId = chatIdInput.trim())) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Text("Сохранить", color = NeonCyan)
                    }

                    Button(
                        onClick = { onTestTelegram(botTokenInput, chatIdInput) },
                        enabled = !isBusy,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                    ) {
                        Text("Тест Telegram", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Live Logs
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Журнал сессии (${logs.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                IconButton(onClick = onClearLogs) {
                    Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = TextDim)
                }
            }
        }

        items(logs, key = { it.id }) { log ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0C131D),
                border = BorderStroke(1.dp, Color(0x3338BDF8))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        DarkNeonStatusBadge(
                            text = log.category,
                            color = when (log.category) {
                                "TICKET_TP" -> NeonAmber
                                "RAISE" -> NeonCyan
                                "REVIEW" -> NeonEmerald
                                else -> if (log.isSuccess) NeonEmerald else NeonCoral
                            }
                        )
                        Text(text = logTimeFormat.format(Date(log.timestamp)), fontFamily = JetBrainsMonoFontFamily, fontSize = 11.sp, color = TextDim)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = log.title, style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextWhite)
                    Text(text = log.details, style = MaterialTheme.typography.bodyMedium, fontSize = 11.sp, color = TextLight)
                }
            }
        }
    }
}
