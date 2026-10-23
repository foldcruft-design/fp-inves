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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.DarkNeonGlassCard
import com.example.ui.components.DarkNeonStatusBadge
import com.example.ui.components.DarkTemplateVariableChipsRow
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextWhite
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
    var askReviewText by remember(settings.askReviewTemplate) { mutableStateOf(settings.askReviewTemplate) }
    var reviewReplyText by remember(settings.reviewReplyTemplate) { mutableStateOf(settings.reviewReplyTemplate) }
    var tpTicketText by remember(settings.autoTicketTpTemplate) { mutableStateOf(settings.autoTicketTpTemplate) }
    var thresholdHoursSlider by remember(settings.unconfirmedThresholdHours) {
        mutableFloatStateOf(settings.unconfirmedThresholdHours.toFloat())
    }

    val orderVariables = listOf(
        "{buyer}" to "Покупатель",
        "{order_id}" to "ID заказа",
        "{lot}" to "Лот",
        "{price}" to "Сумма ₽",
        "{hours_wait}" to "Часов"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("orders_tp_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Auto-Ticket to TP Card
        item {
            DarkNeonGlassCard(elevated = true, accentBorderColor = NeonAmber) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            DarkNeonStatusBadge(text = "АВТОТИКЕТ В ТП (> 1 ДНЯ)", color = NeonAmber)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Авто-тикет в ТП на заказы >24ч", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(text = "Если покупатель не подтверждает > ${thresholdHoursSlider.roundToInt()}ч — бот пишет в ТП", style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = TextLight)
                        }
                    }
                    Switch(
                        checked = settings.isAutoTicketTpEnabled,
                        onCheckedChange = {
                            onSaveSettings(settings.copy(isAutoTicketTpEnabled = it, autoTicketTpTemplate = tpTicketText, unconfirmedThresholdHours = thresholdHoursSlider.roundToInt()))
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonAmber)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Порог: ${thresholdHoursSlider.roundToInt()} часов (1 сутки)", style = MaterialTheme.typography.labelLarge, color = TextWhite)
                Slider(
                    value = thresholdHoursSlider,
                    onValueChange = { thresholdHoursSlider = it },
                    valueRange = 12f..72f,
                    steps = 11,
                    colors = SliderDefaults.colors(thumbColor = NeonAmber, activeTrackColor = NeonAmber)
                )

                DarkTemplateVariableChipsRow(variables = orderVariables, onInsertVariable = { tpTicketText = "$tpTicketText $it" })

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tpTicketText,
                    onValueChange = { tpTicketText = it },
                    label = { Text("Текст запроса в ТП FunPay") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onSaveSettings(settings.copy(unconfirmedThresholdHours = thresholdHoursSlider.roundToInt(), autoTicketTpTemplate = tpTicketText)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Text("Сохранить", color = NeonCyan)
                    }

                    Button(
                        onClick = onScanAllOverdueOrdersForTp,
                        enabled = !isBusy,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber, contentColor = Color.Black)
                    ) {
                        Text("Отправить в ТП", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Review Request & Reply Card
        item {
            DarkNeonGlassCard(accentBorderColor = NeonEmerald) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Просьба отзыва и автоответ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text(text = "Благодарность после подтверждения + ответ на отзыв", style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = TextLight)
                    }
                    Switch(
                        checked = settings.isAskReviewAfterConfirmEnabled,
                        onCheckedChange = { onSaveSettings(settings.copy(isAskReviewAfterConfirmEnabled = it, askReviewTemplate = askReviewText)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonEmerald)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = askReviewText,
                    onValueChange = { askReviewText = it },
                    label = { Text("Шаблон просьбы отзыва") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onSaveSettings(settings.copy(askReviewTemplate = askReviewText, reviewReplyTemplate = reviewReplyText)) },
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black)
                ) {
                    Text("Сохранить шаблоны", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 3. Orders List Header
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Заказы (${orders.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                OutlinedButton(
                    onClick = { onCreateTestOrder("Buyer_${(10..99).random()}", "Игровая валюта (Выдано)", 1650.0, 27) },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NeonCyan)
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Заказ (27ч)", color = NeonCyan, fontSize = 12.sp)
                }
            }
        }

        // 4. Orders Items
        items(orders, key = { it.orderId }) { order ->
            val hoursElapsed = ((System.currentTimeMillis() - order.createdAtTimestamp) / 3600_000L).coerceAtLeast(1L)
            val isOverdue = order.status == "PAID" && hoursElapsed >= settings.unconfirmedThresholdHours

            DarkNeonGlassCard(
                accentBorderColor = when {
                    order.isSupportTicketSent -> NeonCyan
                    isOverdue -> NeonAmber
                    order.status == "CLOSED" -> NeonEmerald
                    else -> null
                }
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "#${order.orderId} • ${order.buyerName}", fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, color = TextWhite)
                    DarkNeonStatusBadge(
                        text = when {
                            order.status == "CLOSED" -> "ЗАКРЫТ"
                            order.isSupportTicketSent -> "ТИКЕТ: ${order.supportTicketId}"
                            isOverdue -> "НЕ ПОДТВЕРЖДЕН ${hoursElapsed}Ч (>24Ч)"
                            else -> "ОПЛАЧЕН (${hoursElapsed}Ч)"
                        },
                        color = when {
                            order.status == "CLOSED" -> NeonEmerald
                            order.isSupportTicketSent -> NeonCyan
                            isOverdue -> NeonAmber
                            else -> NeonCyan
                        }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = order.lotDescription, style = MaterialTheme.typography.bodyMedium, color = TextLight)
                Text(text = "Сумма: ${order.priceRub.toInt()} ₽", fontFamily = JetBrainsMonoFontFamily, fontSize = 12.sp, color = NeonCyan)

                if (order.status == "PAID") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (!order.isSupportTicketSent) {
                            Button(
                                onClick = { onSendTicketToTp(order) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonAmber, contentColor = Color.Black)
                            ) {
                                Text("Автотикет в ТП", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        OutlinedButton(
                            onClick = { onConfirmOrderAndReview(order) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, NeonEmerald)
                        ) {
                            Text("Подтвердить + Отзыв", color = NeonEmerald, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
