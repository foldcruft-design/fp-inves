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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutomationSettingsEntity
import com.example.data.DeviceNodeSessionEntity
import com.example.data.SessionLogEntity
import com.example.ui.components.DarkNeonGlassCard
import com.example.ui.components.DarkNeonStatusBadge
import com.example.ui.components.PureGlassButton
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DarkNeonStatusBadge(
                    text = "РЕЖИМ СТАРОГО ТЕЛЕФОНА 24/7 (АНТИ-ВЫГОРАНИЕ)",
                    color = NeonEmerald
                )
                Text(
                    text = "FP GlassHelper Node",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextWhite
                )
                Text(
                    text = if (settings.isSessionLoopRunning) {
                        "● Сессия активна • Поднятие лотов и тикеты в ТП работают"
                    } else {
                        "○ Сессия на паузе"
                    },
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.5.sp,
                    color = NeonEmerald
                )
                Text(
                    text = "Нажмите в любом месте экрана для выхода",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("server_telegram_screen_list"),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Dedicated Old Device 24/7 Server Master Card
        item {
            DarkNeonGlassCard(elevated = true) {
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
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DarkNeonStatusBadge(
                                    text = "СЕРВЕР 24/7 (СТАРЫЙ ДЕВАЙС)",
                                    color = NeonEmerald
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Круглосуточная сессия",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite
                            )
                            Text(
                                text = "Вход на аккаунт, вечный онлайн, подъем лотов и ТП",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 10.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = TextLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Удерживать CPU и экран (WakeLock)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 11.5.sp,
                        color = TextWhite
                    )
                    Switch(
                        checked = settings.isKeepScreenAwake,
                        onCheckedChange = { onSaveSettings(settings.copy(isKeepScreenAwake = it)) },
                        modifier = Modifier.scale(0.82f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonEmerald.copy(alpha = 0.75f),
                            uncheckedThumbColor = TextDim,
                            uncheckedTrackColor = Color(0x33FFFFFF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PureGlassButton(
                        onClick = { onToggleSession24x7(!settings.isSessionLoopRunning) },
                        accentTint = if (settings.isSessionLoopRunning) NeonCoral else NeonEmerald,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (settings.isSessionLoopRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (settings.isSessionLoopRunning) "Остановить" else "Запустить 24/7",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    PureGlassButton(
                        onClick = { showOledFullscreenOverlay = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brightness2,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "OLED Заставка",
                            color = TextWhite,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 2. Multi-Device Sessions Manager
        item {
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Сессии устройств (${deviceNodes.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite
                        )
                        Text(
                            text = "Выберите действия для каждого старого смартфона",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = TextLight
                        )
                    }

                    PureGlassButton(
                        onClick = { showAddDeviceDialog = !showAddDeviceDialog }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Сессия", color = TextWhite, fontSize = 11.sp)
                    }
                }

                AnimatedVisibility(visible = showAddDeviceDialog) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = newDeviceName,
                            onValueChange = { newDeviceName = it },
                            label = { Text("Название (напр.: Старый девайс Xiaomi)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = newDeviceRole,
                            onValueChange = { newDeviceRole = it },
                            label = { Text("Роль (напр.: Поднятие лотов 24/7)", fontSize = 11.sp) },
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
                                if (newDeviceName.isNotBlank()) {
                                    onAddDeviceNode(newDeviceName, newDeviceRole)
                                    newDeviceName = ""
                                    newDeviceRole = ""
                                    showAddDeviceDialog = false
                                }
                            },
                            accentTint = NeonEmerald,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Добавить сессию устройства", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = TextWhite)
                        }
                    }
                }
            }
        }

        // Device Nodes Items
        items(deviceNodes, key = { it.id }) { node ->
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = node.deviceName,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = TextWhite
                            )
                        }
                        Text(
                            text = node.deviceRole,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = TextLight
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = node.isSessionActive,
                            onCheckedChange = { onUpdateDeviceNode(node.copy(isSessionActive = it)) },
                            modifier = Modifier.scale(0.8f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonEmerald.copy(alpha = 0.75f),
                                uncheckedThumbColor = TextDim,
                                uncheckedTrackColor = Color(0x33FFFFFF)
                            )
                        )
                        IconButton(
                            onClick = { onDeleteDeviceNode(node) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = NeonCoral,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Выполняемые задачи:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextCyan
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = node.taskAutoRaiseLots,
                            onCheckedChange = { onUpdateDeviceNode(node.copy(taskAutoRaiseLots = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                        )
                        Text(text = "Лоты", fontSize = 11.sp, color = TextLight)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = node.taskAutoReplyChat,
                            onCheckedChange = { onUpdateDeviceNode(node.copy(taskAutoReplyChat = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                        )
                        Text(text = "Чат", fontSize = 11.sp, color = TextLight)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.2f)) {
                        Checkbox(
                            checked = node.taskAutoTpTickets,
                            onCheckedChange = { onUpdateDeviceNode(node.copy(taskAutoTpTickets = it)) },
                            colors = CheckboxDefaults.colors(checkedColor = NeonAmber)
                        )
                        Text(text = "ТП (>24ч)", fontSize = 11.sp, color = TextLight)
                    }
                }
            }
        }

        // 3. Telegram Integration Card
        item {
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Telegram Уведомления",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite
                        )
                        Text(
                            text = "Отчеты о заказах, лотах и ТП в реальном времени",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = TextLight
                        )
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
                        modifier = Modifier.scale(0.82f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonCyan.copy(alpha = 0.75f),
                            uncheckedThumbColor = TextDim,
                            uncheckedTrackColor = Color(0x33FFFFFF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = botTokenInput,
                    onValueChange = { botTokenInput = it },
                    label = { Text("Bot Token (@BotFather)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = chatIdInput,
                    onValueChange = { chatIdInput = it },
                    label = { Text("Chat ID (@userinfobot)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PureGlassButton(
                        onClick = {
                            onSaveSettings(
                                settings.copy(
                                    isTelegramEnabled = true,
                                    telegramBotToken = botTokenInput.trim(),
                                    telegramChatId = chatIdInput.trim()
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Сохранить", color = TextWhite, fontSize = 11.5.sp)
                    }

                    PureGlassButton(
                        onClick = { onTestTelegram(botTokenInput, chatIdInput) },
                        enabled = !isBusy,
                        accentTint = NeonCyan,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Тест Telegram", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 11.5.sp)
                    }
                }
            }
        }

        // 4. Live Logs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Журнал сессии (${logs.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite
                )
                IconButton(
                    onClick = onClearLogs,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = TextDim,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        items(logs, key = { it.id }) { log ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0x14FFFFFF),
                border = BorderStroke(0.8.dp, Color(0x22FFFFFF))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DarkNeonStatusBadge(
                            text = log.category,
                            color = when (log.category) {
                                "TICKET_TP" -> NeonAmber
                                "RAISE" -> NeonCyan
                                "REVIEW" -> NeonEmerald
                                else -> if (log.isSuccess) NeonEmerald else NeonCoral
                            }
                        )
                        Text(
                            text = logTimeFormat.format(Date(log.timestamp)),
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.sp,
                            color = TextDim
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = log.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 12.sp,
                        color = TextWhite
                    )
                    Text(
                        text = log.details,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 10.5.sp,
                        color = TextLight
                    )
                }
            }
        }
    }
}
