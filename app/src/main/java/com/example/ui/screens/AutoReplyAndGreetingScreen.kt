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
import com.example.data.AutoReplyRuleEntity
import com.example.data.AutomationSettingsEntity
import com.example.ui.components.DarkNeonGlassCard
import com.example.ui.components.DarkNeonStatusBadge
import com.example.ui.components.DarkTemplateVariableChipsRow
import com.example.ui.components.PureGlassButton
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
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Greeting Card
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
                            imageVector = Icons.Default.WavingHand,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Приветствие в чате",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite
                            )
                            Text(
                                text = "Встречает покупателя при первом сообщении",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 10.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = TextLight
                            )
                        }
                    }
                    Switch(
                        checked = settings.isGreetingEnabled,
                        onCheckedChange = {
                            onSaveSettings(settings.copy(isGreetingEnabled = it, greetingTemplate = greetingText))
                        },
                        modifier = Modifier.scale(0.82f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonEmerald.copy(alpha = 0.75f),
                            uncheckedThumbColor = TextDim,
                            uncheckedTrackColor = Color(0x33FFFFFF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                DarkTemplateVariableChipsRow(
                    variables = availableVariables,
                    onInsertVariable = { greetingText = "$greetingText $it" }
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = greetingText,
                    onValueChange = { greetingText = it },
                    label = { Text("Шаблон приветствия", fontSize = 11.sp) },
                    minLines = 2,
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = settings.greetingOnlyNewBuyers,
                            onCheckedChange = {
                                onSaveSettings(settings.copy(greetingOnlyNewBuyers = it, greetingTemplate = greetingText))
                            },
                            colors = CheckboxDefaults.colors(checkedColor = NeonCyan)
                        )
                        Text(text = "Только новым", fontSize = 11.sp, color = TextLight)
                    }

                    PureGlassButton(
                        onClick = { onSaveSettings(settings.copy(greetingTemplate = greetingText)) },
                        accentTint = NeonCyan
                    ) {
                        Text("Сохранить", fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp, color = TextWhite)
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
                        Text(
                            text = "Правила автоответчика",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite
                        )
                        Text(
                            text = "Мгновенные ответы по ключевым словам",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.5.sp,
                            color = TextDim
                        )
                    }
                    PureGlassButton(
                        onClick = { showAddRuleForm = !showAddRuleForm }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Правило", color = TextWhite, fontSize = 11.sp)
                    }
                }

                AnimatedVisibility(visible = showAddRuleForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = triggerKeywordsInput,
                            onValueChange = { triggerKeywordsInput = it },
                            label = { Text("Ключевые слова через запятую", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = responseTemplateInput,
                            onValueChange = { responseTemplateInput = it },
                            label = { Text("Ответ бота", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        PureGlassButton(
                            onClick = {
                                if (triggerKeywordsInput.isNotBlank() && responseTemplateInput.isNotBlank()) {
                                    onSaveRule(
                                        AutoReplyRuleEntity(
                                            triggerKeywords = triggerKeywordsInput.trim(),
                                            responseTemplate = responseTemplateInput.trim(),
                                            isExactMatch = false,
                                            isEnabled = true
                                        )
                                    )
                                    triggerKeywordsInput = ""
                                    responseTemplateInput = ""
                                    showAddRuleForm = false
                                }
                            },
                            accentTint = NeonEmerald,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Добавить правило", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = TextWhite)
                        }
                    }
                }
            }
        }

        // Rules List
        items(rules, key = { it.id }) { rule ->
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        DarkNeonStatusBadge(text = "Слова: ${rule.triggerKeywords}", color = NeonCyan)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = rule.responseTemplate,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 12.sp,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Сработало: ${rule.triggeredCount} раз",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 9.5.sp,
                            color = TextDim
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Switch(
                            checked = rule.isEnabled,
                            onCheckedChange = { onToggleRule(rule, it) },
                            modifier = Modifier.scale(0.8f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonEmerald.copy(alpha = 0.75f),
                                uncheckedThumbColor = TextDim,
                                uncheckedTrackColor = Color(0x33FFFFFF)
                            )
                        )
                        IconButton(
                            onClick = { onDeleteRule(rule) },
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
            }
        }

        // Live Chat Sandbox
        item {
            DarkNeonGlassCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = NeonEmerald,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Проверка ответа в чате",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = simIncomingText,
                        onValueChange = { simIncomingText = it },
                        label = { Text("Вопрос покупателя", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    PureGlassButton(
                        onClick = { onTestChatMessage(simBuyerName, simIncomingText, "10000 Золота", simIsFirstMessage) },
                        accentTint = NeonEmerald
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                if (simulatorReplies.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    simulatorReplies.forEach { reply ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x1FFFFFFF),
                            border = BorderStroke(0.8.dp, NeonEmerald.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "Бот: $reply",
                                modifier = Modifier.padding(8.dp),
                                color = TextWhite,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
