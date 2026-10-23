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
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.GlassStatusBadge
import com.example.ui.theme.GlassAccentDanger
import com.example.ui.theme.GlassAccentMint
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.TextSlateDark
import com.example.ui.theme.TextSlateMedium
import com.example.ui.theme.TextSlateMuted
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
        // Master Auto-Raise & Eternal Online Card
        item {
            FrostedGlassCard(elevated = true, accentBorderColor = GlassPrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        GlassStatusBadge(
                            text = if (settings.isAutoRaiseMasterEnabled) "АВТОПОДНЯТИЕ АКТИВНО" else "НА ПАУЗЕ",
                            color = if (settings.isAutoRaiseMasterEnabled) GlassAccentMint else TextSlateMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Поднятие лотов в ТОП 24/7",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextSlateDark
                        )
                        Text(
                            text = "Автоматическая отправка POST /lots/raise по таймеру каждого раздела + поддержание статуса Онлайн",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSlateMedium
                        )
                    }
                    Switch(
                        checked = settings.isAutoRaiseMasterEnabled,
                        onCheckedChange = {
                            onUpdateSettings(settings.copy(isAutoRaiseMasterEnabled = it))
                        },
                        modifier = Modifier.testTag("master_auto_raise_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GlassAccentMint
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
                            .height(48.dp)
                            .testTag("raise_all_lots_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Поднять все сейчас", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { showAddDialog = !showAddDialog },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("add_lot_category_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, GlassPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить раздел",
                            tint = GlassPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Раздел", color = GlassPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                // Add New Lot Category Form
                AnimatedVisibility(visible = showAddDialog) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Добавить категорию / раздел FunPay для автоподнятия",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSlateDark
                        )
                        OutlinedTextField(
                            value = gameName,
                            onValueChange = { gameName = it },
                            label = { Text("Название игры (например: Dota 2 / Roblox)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lot_game_name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = categoryTitle,
                            onValueChange = { categoryTitle = it },
                            label = { Text("Раздел (например: Предметы / Аккаунты / Буст)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lot_category_title_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = gameId,
                                onValueChange = { gameId = it },
                                label = { Text("game_id") },
                                placeholder = { Text("142") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = nodeId,
                                onValueChange = { nodeId = it },
                                label = { Text("node_id") },
                                placeholder = { Text("2840") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = lotsCountStr,
                                onValueChange = { lotsCountStr = it },
                                label = { Text("Кол-во лотов") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = intervalMinStr,
                                onValueChange = { intervalMinStr = it },
                                label = { Text("Интервал (мин)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Button(
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_lot_category_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                        ) {
                            Text("Сохранить в очередь автоподнятия", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Individual Lot Categories List
        items(lotCategories, key = { it.id }) { category ->
            FrostedGlassCard(
                accentBorderColor = if (category.isAutoRaiseEnabled) GlassPrimary else null
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category.gameName,
                            style = MaterialTheme.typography.labelLarge,
                            color = GlassPrimary
                        )
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 17.sp,
                            color = TextSlateDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "game_id=${category.gameId} • node_id=${category.nodeId} • Лотов: ${category.lotsCount} шт.",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = TextSlateMuted
                        )
                    }

                    Switch(
                        checked = category.isAutoRaiseEnabled,
                        onCheckedChange = { onToggleCategory(category, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GlassAccentMint
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xD9F8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = GlassPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val lastTimeStr = if (category.lastRaisedAt > 0) {
                                timeFormat.format(Date(category.lastRaisedAt))
                            } else {
                                "Еще не поднимался"
                            }
                            Text(
                                text = "Посл.: $lastTimeStr • Поднятий: ${category.totalRaisesCount}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = TextSlateMedium
                            )
                        }

                        Text(
                            text = "Каждые ${category.intervalMinutes} мин",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = GlassPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Статус: ${category.lastStatusMessage}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    color = TextSlateMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onRaiseSingleNow(category) },
                        enabled = !isBusy,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GlassPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = GlassPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Поднять вне очереди", fontSize = 12.sp, color = GlassPrimary)
                    }

                    IconButton(onClick = { onDeleteCategory(category) }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Удалить раздел",
                            tint = GlassAccentDanger
                        )
                    }
                }
            }
        }
    }
}
