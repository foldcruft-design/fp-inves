package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutomationSettingsEntity
import com.example.data.LotCategoryEntity
import com.example.ui.components.DarkNeonGlassCard
import com.example.ui.components.DarkNeonStatusBadge
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.JetBrainsMonoFontFamily
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
fun LotsAutoRaiseScreen(
    lotCategories: List<LotCategoryEntity>,
    settings: AutomationSettingsEntity,
    isBusy: Boolean,
    onUpdateSettings: (AutomationSettingsEntity) -> Unit,
    onAddCategory: (String, String, String, String, Int, Int) -> Unit,
    onToggleCategory: (LotCategoryEntity, Boolean) -> Unit,
    onDeleteCategory: (LotCategoryEntity) -> Unit,
    onRaiseSingleNow: (LotCategoryEntity) -> Unit,
    onRaiseAllNow: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var gameName by remember { mutableStateOf("") }
    var categoryTitle by remember { mutableStateOf("") }
    var gameId by remember { mutableStateOf("") }
    var nodeId by remember { mutableStateOf("") }
    var lotsCountStr by remember { mutableStateOf("5") }
    var intervalMinStr by remember { mutableStateOf("180") }

    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("lots_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Master Auto-Raise Switch
        item {
            DarkNeonGlassCard(elevated = true, accentBorderColor = NeonCyan) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        DarkNeonStatusBadge(
                            text = if (settings.isAutoRaiseMasterEnabled) "АВТОПОДНЯТИЕ 24/7 ВКЛЮЧЕНО" else "ПАУЗА",
                            color = if (settings.isAutoRaiseMasterEnabled) NeonEmerald else TextDim
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Поднятие лотов в ТОП",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Авто-вызов /lots/raise по расписанию для каждой игры",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextLight
                        )
                    }
                    Switch(
                        checked = settings.isAutoRaiseMasterEnabled,
                        onCheckedChange = {
                            onUpdateSettings(settings.copy(isAutoRaiseMasterEnabled = it))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonEmerald,
                            uncheckedThumbColor = TextDim,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onRaiseAllNow,
                        enabled = !isBusy,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                    ) {
                        Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Поднять все сейчас", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showAddDialog = !showAddDialog },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Раздел", color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                }

                AnimatedVisibility(visible = showAddDialog) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = gameName,
                            onValueChange = { gameName = it },
                            label = { Text("Игра (напр. Dota 2, CS2, WoW)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = categoryTitle,
                            onValueChange = { categoryTitle = it },
                            label = { Text("Название раздела (Предметы / Валюта)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = gameId,
                                onValueChange = { gameId = it },
                                label = { Text("game_id") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                            )
                            OutlinedTextField(
                                value = nodeId,
                                onValueChange = { nodeId = it },
                                label = { Text("node_id") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                            )
                        }
                        Button(
                            onClick = {
                                onAddCategory(gameId, nodeId, categoryTitle, gameName, lotsCountStr.toIntOrNull() ?: 5, intervalMinStr.toIntOrNull() ?: 180)
                                gameName = ""
                                categoryTitle = ""
                                gameId = ""
                                nodeId = ""
                                showAddDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                        ) {
                            Text("Сохранить категорию", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // List of categories
        items(lotCategories, key = { it.id }) { category ->
            DarkNeonGlassCard(
                accentBorderColor = if (category.isAutoRaiseEnabled) NeonCyan else null
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = category.gameName, style = MaterialTheme.typography.labelLarge, color = TextCyan)
                        Text(text = category.title, style = MaterialTheme.typography.titleMedium, fontSize = 16.sp, color = TextWhite)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "node_id=${category.nodeId} • Лотов: ${category.lotsCount} шт.",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = TextDim
                        )
                    }
                    Switch(
                        checked = category.isAutoRaiseEnabled,
                        onCheckedChange = { onToggleCategory(category, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonEmerald,
                            uncheckedThumbColor = TextDim,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF090E16),
                    border = BorderStroke(1.dp, Color(0x3338BDF8))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            val lastTime = if (category.lastRaisedAt > 0) timeFormat.format(Date(category.lastRaisedAt)) else "Не поднимался"
                            Text(text = "Посл.: $lastTime • Поднятий: ${category.totalRaisesCount}", style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = TextLight)
                        }
                        Text(text = "Каждые ${category.intervalMinutes} мин", fontFamily = JetBrainsMonoFontFamily, fontSize = 11.sp, color = NeonCyan)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onRaiseSingleNow(category) },
                        enabled = !isBusy,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Text("Поднять сейчас", color = NeonCyan, fontSize = 12.sp)
                    }

                    IconButton(onClick = { onDeleteCategory(category) }) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Удалить", tint = NeonCoral)
                    }
                }
            }
        }
    }
}
