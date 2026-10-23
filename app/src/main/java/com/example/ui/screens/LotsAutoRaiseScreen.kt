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
import com.example.data.LotCategoryEntity
import com.example.ui.components.DarkNeonGlassCard
import com.example.ui.components.DarkNeonStatusBadge
import com.example.ui.components.PureGlassButton
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
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Master Auto-Raise Card
        item {
            DarkNeonGlassCard(elevated = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        DarkNeonStatusBadge(
                            text = if (settings.isAutoRaiseMasterEnabled) "АВТОПОДНЯТИЕ 24/7" else "ПАУЗА",
                            color = if (settings.isAutoRaiseMasterEnabled) NeonEmerald else TextDim
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Поднятие лотов в ТОП",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextWhite
                        )
                        Text(
                            text = "Фоновый вызов POST /lots/raise по таймеру сессии",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = TextLight
                        )
                    }
                    Switch(
                        checked = settings.isAutoRaiseMasterEnabled,
                        onCheckedChange = {
                            onUpdateSettings(settings.copy(isAutoRaiseMasterEnabled = it))
                        },
                        modifier = Modifier
                            .scale(0.85f)
                            .testTag("master_auto_raise_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonEmerald.copy(alpha = 0.75f),
                            uncheckedThumbColor = TextDim,
                            uncheckedTrackColor = Color(0x33FFFFFF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

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
                            .testTag("raise_all_lots_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Поднять все сейчас",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                    }

                    PureGlassButton(
                        onClick = { showAddDialog = !showAddDialog },
                        modifier = Modifier.testTag("add_lot_category_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Раздел", color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                AnimatedVisibility(visible = showAddDialog) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = gameName,
                            onValueChange = { gameName = it },
                            label = { Text("Игра (напр. Dota 2, CS2)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = categoryTitle,
                            onValueChange = { categoryTitle = it },
                            label = { Text("Название раздела (Предметы / Валюта)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = gameId,
                                onValueChange = { gameId = it },
                                label = { Text("game_id", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )
                            OutlinedTextField(
                                value = nodeId,
                                onValueChange = { nodeId = it },
                                label = { Text("node_id", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                )
                            )
                        }
                        PureGlassButton(
                            onClick = {
                                onAddCategory(
                                    gameId,
                                    nodeId,
                                    categoryTitle,
                                    gameName,
                                    lotsCountStr.toIntOrNull() ?: 5,
                                    intervalMinStr.toIntOrNull() ?: 180
                                )
                                gameName = ""
                                categoryTitle = ""
                                gameId = ""
                                nodeId = ""
                                showAddDialog = false
                            },
                            accentTint = NeonEmerald,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Сохранить категорию", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                }
            }
        }

        // List of Categories
        items(lotCategories, key = { it.id }) { category ->
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category.gameName,
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 10.5.sp,
                            color = TextCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 13.5.sp,
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "node_id=${category.nodeId} • Лотов: ${category.lotsCount} шт.",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.sp,
                            color = TextDim
                        )
                    }
                    Switch(
                        checked = category.isAutoRaiseEnabled,
                        onCheckedChange = { onToggleCategory(category, it) },
                        modifier = Modifier.scale(0.82f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonEmerald.copy(alpha = 0.75f),
                            uncheckedThumbColor = TextDim,
                            uncheckedTrackColor = Color(0x33FFFFFF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(7.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x14FFFFFF),
                    border = BorderStroke(0.8.dp, Color(0x22FFFFFF))
                ) {
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
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            val lastTime = if (category.lastRaisedAt > 0) {
                                timeFormat.format(Date(category.lastRaisedAt))
                            } else {
                                "Ожидание"
                            }
                            Text(
                                text = "Посл.: $lastTime • Поднятий: ${category.totalRaisesCount}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 10.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = TextLight
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${category.intervalMinutes} мин",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.sp,
                            color = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PureGlassButton(
                        onClick = { onRaiseSingleNow(category) },
                        enabled = !isBusy,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Поднять сейчас", color = TextWhite, fontSize = 11.5.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { onDeleteCategory(category) },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Удалить",
                            tint = NeonCoral,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
