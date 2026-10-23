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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.WavingHand
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
import com.example.data.AutoReplyRuleEntity
import com.example.data.AutomationSettingsEntity
import com.example.ui.components.DarkNeonGlassCard
import com.example.ui.components.DarkNeonStatusBadge
import com.example.ui.components.DarkTemplateVariableChipsRow
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextWhite

@Composable
fun AutoReplyAndGreetingScreen(
    settings: AutomationSettingsEntity,
    rules: List<AutoReplyRuleEntity>,
    simulatorReplies: List<String>,
    onSaveSettings: (AutomationSettingsEntity) -> Unit,
    onSaveRule: (AutoReplyRuleEntity) -> Unit,
    onToggleRule: (AutoReplyRuleEntity, Boolean) -> Unit,
    onDeleteRule: (AutoReplyRuleEntity) -> Unit,
    onTestChatMessage: (String, String, String, Boolean) -> Unit,
    onPreviewTemplate: (String) -> String
) {
    var greetingText by remember(settings.greetingTemplate) { mutableStateOf(settings.greetingTemplate) }
    var showAddRuleForm by remember { mutableStateOf(false) }
    var triggerKeywordsInput by remember { mutableStateOf("") }
    var responseTemplateInput by remember { mutableStateOf("") }
    var exactMatchInput by remember { mutableStateOf(false) }

    var simBuyerName by remember { mutableStateOf("DragonBuyer") }
    var simIncomingText by remember { mutableStateOf("Привет, тут? Актуально?") }
    var simIsFirstMessage by remember { mutableStateOf(true) }

    val availableVariables = listOf(
        "{buyer}" to "Покупатель",
        "{lot}" to "Лот",
        "{time}" to "Время",
        "{price}" to "Цена"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("auto_reply_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Greeting Card
        item {
            DarkNeonGlassCard(elevated = true, accentBorderColor = NeonCyan) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.WavingHand, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Приветствие в чате FunPay", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(text = "Встречает покупателя при первом обращении", style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = TextLight)
                        }
                    }
                    Switch(
                        checked = settings.isGreetingEnabled,
                        onCheckedChange = {
                            onSaveSettings(settings.copy(isGreetingEnabled = it, greetingTemplate = greetingText))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonEmerald)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                DarkTemplateVariableChipsRow(variables = availableVariables, onInsertVariable = { greetingText = "$greetingText $it" })

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = greetingText,
                    onValueChange = { greetingText = it },
                    label = { Text("Текст приветствия") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = settings.greetingOnlyNewBuyers,
                            onCheckedChange = { onSaveSettings(settings.copy(greetingOnlyNewBuyers = it, greetingTemplate = greetingText)) },
                            colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                        )
                        Text(text = "Только новым", fontSize = 12.sp, color = TextLight)
                    }

                    Button(
                        onClick = { onSaveSettings(settings.copy(greetingTemplate = greetingText)) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                    ) {
                        Text("Сохранить", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. Rules Header & Add Form
        item {
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Правила автоответчика", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text(text = "Ответы по ключевым словам («тут?», «скидка»)", style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = TextDim)
                    }
                    OutlinedButton(
                        onClick = { showAddRuleForm = !showAddRuleForm },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Правило", color = NeonCyan, fontSize = 12.sp)
                    }
                }

                AnimatedVisibility(visible = showAddRuleForm) {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = triggerKeywordsInput,
                            onValueChange = { triggerKeywordsInput = it },
                            label = { Text("Ключевые слова через запятую") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = responseTemplateInput,
                            onValueChange = { responseTemplateInput = it },
                            label = { Text("Ответ бота") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        Button(
                            onClick = {
                                if (triggerKeywordsInput.isNotBlank() && responseTemplateInput.isNotBlank()) {
                                    onSaveRule(AutoReplyRuleEntity(triggerKeywords = triggerKeywordsInput.trim(), responseTemplate = responseTemplateInput.trim(), isExactMatch = exactMatchInput, isEnabled = true))
                                    triggerKeywordsInput = ""
                                    responseTemplateInput = ""
                                    showAddRuleForm = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                        ) {
                            Text("Добавить правило", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Rules List
        items(rules, key = { it.id }) { rule ->
            DarkNeonGlassCard(accentBorderColor = if (rule.isEnabled) NeonCyan else null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        DarkNeonStatusBadge(text = "Триггеры: ${rule.triggerKeywords}", color = NeonCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = rule.responseTemplate, style = MaterialTheme.typography.bodyLarge, color = TextWhite)
                        Text(text = "Сработало: ${rule.triggeredCount} раз", fontFamily = JetBrainsMonoFontFamily, fontSize = 11.sp, color = TextDim)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Switch(
                            checked = rule.isEnabled,
                            onCheckedChange = { onToggleRule(rule, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonEmerald)
                        )
                        IconButton(onClick = { onDeleteRule(rule) }) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, tint = NeonCoral)
                        }
                    }
                }
            }
        }

        // Live Chat Sandbox
        item {
            DarkNeonGlassCard(accentBorderColor = NeonEmerald) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Forum, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Тест-песочница чата", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = simIncomingText,
                        onValueChange = { simIncomingText = it },
                        label = { Text("Сообщение покупателя") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    Button(
                        onClick = { onTestChatMessage(simBuyerName, simIncomingText, "10000 Золота", simIsFirstMessage) },
                        modifier = Modifier.height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
                if (simulatorReplies.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    simulatorReplies.forEach { reply ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF063327),
                            border = BorderStroke(1.dp, NeonEmerald)
                        ) {
                            Text(text = "Ответ бота: $reply", modifier = Modifier.padding(10.dp), color = TextWhite, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
