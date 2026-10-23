package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.FunPayOrderEntity
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.GlassStatusBadge
import com.example.ui.components.TemplateVariableChipsRow
import com.example.ui.theme.GlassAccentMint
import com.example.ui.theme.GlassAccentWarning
import com.example.ui.theme.GlassPrimary
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.TextSlateDark
import com.example.ui.theme.TextSlateMedium
import com.example.ui.theme.TextSlateMuted
import kotlin.math.roundToInt

@Composable
fun OrdersReviewsTicketsScreen(
    orders: List<FunPayOrderEntity>,
    settings: AutomationSettingsEntity,
    isBusy: Boolean,
    onSaveSettings: (AutomationSettingsEntity) -> Unit,
    onConfirmOrderAndReview: (FunPayOrderEntity) -> Unit,
    onSendTicketToTp: (FunPayOrderEntity) -> Unit,
    onScanAllOverdueOrdersForTp: () -> Unit,
    onCreateTestOrder: (String, String, Double, Int) -> Unit
) {
    var askReviewText by remember(settings.askReviewTemplate) {
        mutableStateOf(settings.askReviewTemplate)
    }
    var reviewReplyText by remember(settings.reviewReplyTemplate) {
        mutableStateOf(settings.reviewReplyTemplate)
    }
    var minStarsSlider by remember(settings.minStarsForAutoReviewReply) {
        mutableFloatStateOf(settings.minStarsForAutoReviewReply.toFloat())
    }
    var tpTicketText by remember(settings.autoTicketTpTemplate) {
        mutableStateOf(settings.autoTicketTpTemplate)
    }
    var thresholdHoursSlider by remember(settings.unconfirmedThresholdHours) {
        mutableFloatStateOf(settings.unconfirmedThresholdHours.toFloat())
    }

    val orderVariables = listOf(
        "{buyer}" to "Покупатель",
        "{order_id}" to "ID заказа",
        "{lot}" to "Лот",
        "{price}" to "Сумма ₽",
        "{stars}" to "Оценка ★",
        "{hours_wait}" to "Часов ожидания"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("orders_tp_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Request Review After Order Confirmation Card
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
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            tint = GlassPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Просьба отзыва после подтверждения",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSlateDark
                            )
                            Text(
                                text = "Когда покупатель подтверждает заказ — бот благодарит и просит оставить отзыв",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = TextSlateMedium
                            )
                        }
                    }

                    Switch(
                        checked = settings.isAskReviewAfterConfirmEnabled,
                        onCheckedChange = {
                            onSaveSettings(
                                settings.copy(
                                    isAskReviewAfterConfirmEnabled = it,
                                    askReviewTemplate = askReviewText
                                )
                            )
                        },
                        modifier = Modifier.testTag("ask_review_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GlassAccentMint
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TemplateVariableChipsRow(
                    variables = orderVariables,
                    onInsertVariable = { tag -> askReviewText = "$askReviewText $tag" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = askReviewText,
                    onValueChange = { askReviewText = it },
                    label = { Text("Шаблон просьбы об отзыве после подтверждения заказа") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ask_review_template_input"),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSaveSettings(settings.copy(askReviewTemplate = askReviewText))
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .testTag("save_ask_review_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlassPrimary)
                ) {
                    Text("Сохранить шаблон просьбы отзыва", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. Auto-Reply to Buyer Reviews Card
        item {
            FrostedGlassCard(accentBorderColor = GlassAccentMint) {
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
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GlassAccentWarning,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Автоответ на отзывы покупателей",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSlateDark
                            )
                            Text(
                                text = "Публикует ответ продавца под отзывом на странице заказа",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = TextSlateMedium
                            )
                        }
                    }

                    Switch(
                        checked = settings.isAutoReviewReplyEnabled,
                        onCheckedChange = {
                            onSaveSettings(
                                settings.copy(
                                    isAutoReviewReplyEnabled = it,
                                    reviewReplyTemplate = reviewReplyText,
                                    minStarsForAutoReviewReply = minStarsSlider.roundToInt()
                                )
                            )
                        },
                        modifier = Modifier.testTag("auto_review_reply_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GlassAccentMint
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Отвечать автоматически при оценке от ${minStarsSlider.roundToInt()} ★ до 5 ★:",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSlateDark
                )
                Slider(
                    value = minStarsSlider,
                    onValueChange = { minStarsSlider = it },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = GlassAccentWarning,
                        activeTrackColor = GlassAccentWarning
                    )
                )

                TemplateVariableChipsRow(
                    variables = orderVariables,
                    onInsertVariable = { tag -> reviewReplyText = "$reviewReplyText $tag" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reviewReplyText,
                    onValueChange = { reviewReplyText = it },
                    label = { Text("Шаблон автоответа на отзыв") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_reply_template_input"),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSaveSettings(
                            settings.copy(
                                reviewReplyTemplate = reviewReplyText,
                                minStarsForAutoReviewReply = minStarsSlider.roundToInt()
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .testTag("save_review_reply_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlassAccentMint)
                ) {
                    Text("Сохранить автоответ на отзывы", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 3. Auto-Ticket to FunPay Support for Unconfirmed Orders > 1 Day (24h)
        item {
            FrostedGlassCard(elevated = true, accentBorderColor = GlassAccentWarning) {
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
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = GlassAccentWarning,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            GlassStatusBadge(
                                text = "АВТОТИКЕТ В ТП (> 1 ДНЯ)",
                                color = GlassAccentWarning
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Автотикет в ТП на неподтвержденные заказы",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSlateDark
                            )
                            Text(
                                text = "Если заказ висит оплаченным больше ${thresholdHoursSlider.roundToInt()} ч. (1 дня), бот пишет в ТП FunPay и просит подтвердить!",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = TextSlateMedium
                            )
                        }
                    }

                    Switch(
                        checked = settings.isAutoTicketTpEnabled,
                        onCheckedChange = {
                            onSaveSettings(
                                settings.copy(
                                    isAutoTicketTpEnabled = it,
                                    unconfirmedThresholdHours = thresholdHoursSlider.roundToInt(),
                                    autoTicketTpTemplate = tpTicketText
                                )
                            )
                        },
                        modifier = Modifier.testTag("auto_ticket_tp_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GlassAccentWarning
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Порог ожидания подтверждения: ${thresholdHoursSlider.roundToInt()} ч. (рекомендуется 24 ч. = 1 сутки)",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSlateDark
                )
                Slider(
                    value = thresholdHoursSlider,
                    onValueChange = { thresholdHoursSlider = it },
                    valueRange = 12f..72f,
                    steps = 11,
                    colors = SliderDefaults.colors(
                        thumbColor = GlassAccentWarning,
                        activeTrackColor = GlassAccentWarning
                    )
                )

                TemplateVariableChipsRow(
                    variables = orderVariables,
                    onInsertVariable = { tag -> tpTicketText = "$tpTicketText $tag" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tpTicketText,
                    onValueChange = { tpTicketText = it },
                    label = { Text("Текст обращения в Техподдержку FunPay (support.funpay.com)") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tp_ticket_template_input"),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onSaveSettings(
                                settings.copy(
                                    unconfirmedThresholdHours = thresholdHoursSlider.roundToInt(),
                                    autoTicketTpTemplate = tpTicketText
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GlassPrimary)
                    ) {
                        Text("Сохранить шаблон ТП", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onScanAllOverdueOrdersForTp,
                        enabled = !isBusy,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("run_auto_ticket_scan_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlassAccentWarning)
                    ) {
                        Text(
                            text = "Отправить тикеты (>24ч)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 4. Orders List Header + Add Simulated Overdue Order Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Мониторинг заказов (${orders.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextSlateDark
                    )
                    Text(
                        text = "Заказы старше ${settings.unconfirmedThresholdHours}ч автоматически передаются в ТП",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = TextSlateMuted
                    )
                }

                OutlinedButton(
                    onClick = {
                        onCreateTestOrder(
                            "NightWalker_${(10..99).random()}",
                            "Редкий скин + Бонус (Выдано в чат)",
                            1950.0,
                            27 // 27 hours ago (> 24h)
                        )
                    },
                    modifier = Modifier.testTag("add_overdue_order_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GlassPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Заказ (27ч)", fontSize = 12.sp)
                }
            }
        }

        // 5. Orders Cards
        items(orders, key = { it.orderId }) { order ->
            val hoursElapsed = ((System.currentTimeMillis() - order.createdAtTimestamp) / 3600_000L).coerceAtLeast(1L)
            val isOverdue = order.status == "PAID" && hoursElapsed >= settings.unconfirmedThresholdHours

            FrostedGlassCard(
                accentBorderColor = when {
                    order.isSupportTicketSent -> GlassPrimary
                    isOverdue -> GlassAccentWarning
                    order.status == "CLOSED" -> GlassAccentMint
                    else -> null
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${order.orderId}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GlassPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${order.buyerName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSlateDark
                        )
                    }

                    GlassStatusBadge(
                        text = when {
                            order.status == "CLOSED" -> "ПОДТВЕРЖДЕН"
                            order.isSupportTicketSent -> "ТИКЕТ В ТП: ${order.supportTicketId}"
                            isOverdue -> "НЕ ПОДТВЕРЖДЕН ${hoursElapsed}Ч (>24Ч)"
                            else -> "ОПЛАЧЕН (${hoursElapsed}Ч НАЗАД)"
                        },
                        color = when {
                            order.status == "CLOSED" -> GlassAccentMint
                            order.isSupportTicketSent -> GlassPrimary
                            isOverdue -> GlassAccentWarning
                            else -> GlassPrimary
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = order.lotDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSlateDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Сумма заказа: ${order.priceRub.toInt()} ₽ • Время с момента оплаты: $hoursElapsed ч.",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 12.sp,
                    color = TextSlateMedium
                )

                // Show Review & Ticket Badges
                if (order.hasRequestedReview || order.hasRepliedToReview || order.isSupportTicketSent) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0F9FF),
                        border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            if (order.isSupportTicketSent) {
                                Text(
                                    text = "🛠 Автотикет ${order.supportTicketId} отправлен в ТП FunPay (запрос на закрытие заказа)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 12.sp,
                                    color = GlassPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (order.hasRequestedReview) {
                                Text(
                                    text = "✅ Покупателю отправлена просьба оставить отзыв",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 12.sp,
                                    color = Color(0xFF047857)
                                )
                            }
                            if (order.hasRepliedToReview) {
                                Text(
                                    text = "⭐ Опубликован автоответ продавца на отзыв (${order.reviewStars}★): «${order.reviewText}»",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 12.sp,
                                    color = TextSlateDark
                                )
                            }
                        }
                    }
                }

                // Action Buttons for Unconfirmed ("PAID") Orders
                if (order.status == "PAID") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!order.isSupportTicketSent) {
                            Button(
                                onClick = { onSendTicketToTp(order) },
                                enabled = !isBusy,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("send_tp_ticket_${order.orderId}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isOverdue) GlassAccentWarning else GlassPrimary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Автотикет в ТП",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { onConfirmOrderAndReview(order) },
                            enabled = !isBusy,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("confirm_order_${order.orderId}"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GlassAccentMint)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GlassAccentMint,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Подтвердить + Отзыв",
                                fontSize = 12.sp,
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
