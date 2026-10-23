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
import com.example.data.AutoReplyRuleEntity
import com.example.data.AutomationSettingsEntity
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.GlassStatusBadge
import com.example.ui.components.TemplateVariableChipsRow
import com.example.ui.theme.GlassAccentDanger
import com.example.ui.theme.GlassAccentMint
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.TextSlateDark
import com.example.ui.theme.TextSlateMedium
import com.example.ui.theme.TextSlateMuted

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
    var greetingText by remember(settings.greetingTemplate) {
        mutableStateOf(settings.greetingTemplate)
    }
    var showAddRuleForm by remember { mutableStateOf(false) }
    var triggerKeywordsInput by remember { mutableStateOf("") }
    var responseTemplateInput by remember { mutableStateOf("") }
    var exactMatchInput by remember { mutableStateOf(false) }

    // Interactive Chat Simulator state
    var simBuyerName by remember { mutableStateOf("DragonBuyer") }
    var simIncomingText by remember { mutableStateOf("Привет, тут? Товар актуально?") }
    var simIsFirstMessage by remember { mutableStateOf(true) }

    val availableVariables = listOf(
        "{buyer}" to "Ник покупателя",
        "{lot}" to "Название лота",
        "{time}" to "Текущее время",
        "{price}" to "Цена"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("auto_reply_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Welcome Greeting Configuration Card
        item {
            FrostedGlassCard(elevated = true, accentBorderColor = GlassPrimary) {
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
                            tint = GlassPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Настройка приветствия в чате",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextSlateDark
                            )
                            Text(
                                text = "Автоматически встречает покупателя при первом сообщении",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSlateMedium
                            )
                        }
                    }

                    Switch(
                        checked = settings.isGreetingEnabled,
                        onCheckedChange = {
                            onSaveSettings(settings.copy(isGreetingEnabled = it, greetingTemplate = greetingText))
                        },
                        modifier = Modifier.testTag("greeting_enable_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GlassAccentMint
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Нажмите на переменную, чтобы вставить в текст:",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 12.sp,
                    color = TextSlateMuted
                )
                Spacer(modifier = Modifier.height(6.dp))

                TemplateVariableChipsRow(
                    variables = availableVariables,
                    onInsertVariable = { tag ->
                        greetingText = "$greetingText $tag"
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = greetingText,
                    onValueChange = { greetingText = it },
                    label = { Text("Шаблон приветствия покупателя") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("greeting_template_input"),
                    minLines = 3,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Live Preview Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE0F2FE),
                    border = BorderStroke(1.dp, Color(0xFFbae6fd))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Предпросмотр в чате FunPay:",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlassPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = onPreviewTemplate(greetingText),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSlateDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = settings.greetingOnlyNewBuyers,
                            onCheckedChange = {
                                onSaveSettings(
                                    settings.copy(
                                        greetingOnlyNewBuyers = it,
                                        greetingTemplate = greetingText
                                    )
                                )
                            }
                        )
                        Text(
                            text = "Только при 1-м диалоге",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSlateDark
                        )
                    }

                    Button(
                        onClick = {
                            onSaveSettings(settings.copy(greetingTemplate = greetingText))
                        },
                        modifier = Modifier.testTag("save_greeting_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                    ) {
                        Text("Сохранить приветствие", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. Keyword Auto-Reply Rules Header + Add Form
        item {
            FrostedGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Умный Автоответчик по командам",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextSlateDark
                        )
                        Text(
                            text = "Мгновенно отвечает на вопросы «тут?», «скидка», «актуально» 24/7",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSlateMuted
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddRuleForm = !showAddRuleForm },
                        modifier = Modifier.testTag("add_auto_reply_rule_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, GlassPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить правило",
                            tint = GlassPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Правило", color = GlassPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                AnimatedVisibility(visible = showAddRuleForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = triggerKeywordsInput,
                            onValueChange = { triggerKeywordsInput = it },
                            label = { Text("Ключевые слова через запятую (напр.: гарантия, бан, как купить)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("rule_keywords_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        TemplateVariableChipsRow(
                            variables = availableVariables,
                            onInsertVariable = { tag ->
                                responseTemplateInput = "$responseTemplateInput $tag"
                            }
                        )

                        OutlinedTextField(
                            value = responseTemplateInput,
                            onValueChange = { responseTemplateInput = it },
                            label = { Text("Текст автоответа бота") },
                            minLines = 2,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("rule_response_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = exactMatchInput,
                                    onCheckedChange = { exactMatchInput = it }
                                )
                                Text(
                                    text = "Точное совпадение фразы",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Button(
                                onClick = {
                                    if (triggerKeywordsInput.isNotBlank() && responseTemplateInput.isNotBlank()) {
                                        onSaveRule(
                                            AutoReplyRuleEntity(
                                                triggerKeywords = triggerKeywordsInput.trim(),
                                                responseTemplate = responseTemplateInput.trim(),
                                                isExactMatch = exactMatchInput,
                                                isEnabled = true
                                            )
                                        )
                                        triggerKeywordsInput = ""
                                        responseTemplateInput = ""
                                        showAddRuleForm = false
                                    }
                                },
                                modifier = Modifier.testTag("save_rule_submit_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                            ) {
                                Text("Добавить правило", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. List of Keyword Rules
        items(rules, key = { it.id }) { rule ->
            FrostedGlassCard(
                accentBorderColor = if (rule.isEnabled) GlassPrimary else null
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        GlassStatusBadge(
                            text = "Триггеры: ${rule.triggerKeywords}",
                            color = GlassPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = rule.responseTemplate,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSlateDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Срабатываний в чате: ${rule.triggeredCount} раз",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = TextSlateMuted
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Switch(
                            checked = rule.isEnabled,
                            onCheckedChange = { onToggleRule(rule, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GlassAccentMint
                            )
                        )
                        IconButton(onClick = { onDeleteRule(rule) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Удалить правило",
                                tint = GlassAccentDanger
                            )
                        }
                    }
                }
            }
        }

        // 4. Interactive Buyer Chat Simulator
        item {
            FrostedGlassCard(accentBorderColor = GlassAccentMint) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = GlassAccentMint,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Тест-песочница чата FunPay",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSlateDark
                        )
                        Text(
                            text = "Проверьте, как ваш автоответчик и приветствие ответят покупателю",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp,
                            color = TextSlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = simBuyerName,
                        onValueChange = { simBuyerName = it },
                        label = { Text("Покупатель") },
                        singleLine = true,
                        modifier = Modifier.weight(0.4f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = simIncomingText,
                        onValueChange = { simIncomingText = it },
                        label = { Text("Сообщение покупателя") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(0.6f)
                            .testTag("chat_simulator_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = simIsFirstMessage,
                            onCheckedChange = { simIsFirstMessage = it }
                        )
                        Text("Новый диалог (с приветствием)", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onTestChatMessage(
                                simBuyerName,
                                simIncomingText,
                                "10000 Золота + Бонус",
                                simIsFirstMessage
                            )
                        },
                        modifier = Modifier.testTag("chat_simulator_send_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlassAccentMint)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Проверить ответ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (simulatorReplies.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    simulatorReplies.forEach { reply ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFD1FAE5),
                            border = BorderStroke(1.dp, GlassAccentMint)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Ответ бота → $simBuyerName:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    text = reply,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSlateDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
