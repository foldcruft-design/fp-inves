package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutomationSettingsEntity
import com.example.data.FunPayOrderEntity
import com.example.ui.components.DarkNeonGlassCard
import com.example.ui.components.DarkNeonStatusBadge
import com.example.ui.components.DarkTemplateVariableChipsRow
import com.example.ui.components.PureGlassButton
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
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Auto-Ticket to TP Card
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
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            DarkNeonStatusBadge(text = "АВТОТИКЕТ В ТП (> 1 ДНЯ)", color = NeonAmber)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Авто-тикет в ТП на заказы >24ч",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite
                            )
                            Text(
                                text = "Не подтвержден > ${thresholdHoursSlider.roundToInt()}ч — бот пишет в ТП",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 10.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = TextLight
                            )
                        }
                    }
                    Switch(
                        checked = settings.isAutoTicketTpEnabled,
                        onCheckedChange = {
                            onSaveSettings(
                                settings.copy(
                                    isAutoTicketTpEnabled = it,
                                    autoTicketTpTemplate = tpTicketText,
                                    unconfirmedThresholdHours = thresholdHoursSlider.roundToInt()
                                )
                            )
                        },
                        modifier = Modifier.scale(0.82f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonAmber.copy(alpha = 0.75f),
                            uncheckedThumbColor = TextDim,
                            uncheckedTrackColor = Color(0x33FFFFFF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Порог ожидания: ${thresholdHoursSlider.roundToInt()} ч. (1 сутки)",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 11.sp,
                    color = TextWhite
                )
                Slider(
                    value = thresholdHoursSlider,
                    onValueChange = { thresholdHoursSlider = it },
                    valueRange = 12f..72f,
                    steps = 11,
                    colors = SliderDefaults.colors(thumbColor = NeonAmber, activeTrackColor = NeonAmber)
                )

                DarkTemplateVariableChipsRow(
                    variables = orderVariables,
                    onInsertVariable = { tpTicketText = "$tpTicketText $it" }
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = tpTicketText,
                    onValueChange = { tpTicketText = it },
                    label = { Text("Текст запроса в ТП FunPay", fontSize = 11.sp) },
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PureGlassButton(
                        onClick = {
                            onSaveSettings(
                                settings.copy(
                                    unconfirmedThresholdHours = thresholdHoursSlider.roundToInt(),
                                    autoTicketTpTemplate = tpTicketText
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Сохранить", color = TextWhite, fontSize = 11.5.sp)
                    }

                    PureGlassButton(
                        onClick = onScanAllOverdueOrdersForTp,
                        enabled = !isBusy,
                        accentTint = NeonAmber,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Отправить в ТП", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 11.5.sp)
                    }
                }
            }
        }

        // 2. Review Request & Reply Card
        item {
            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Просьба отзыва и автоответ",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite
                        )
                        Text(
                            text = "После подтверждения заказа + ответ на отзыв",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = TextLight
                        )
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

                OutlinedTextField(
                    value = askReviewText,
                    onValueChange = { askReviewText = it },
                    label = { Text("Шаблон просьбы отзыва", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = reviewReplyText,
                    onValueChange = { reviewReplyText = it },
                    label = { Text("Шаблон автоответа на отзыв", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                PureGlassButton(
                    onClick = {
                        onSaveSettings(
                            settings.copy(
                                askReviewTemplate = askReviewText,
                                reviewReplyTemplate = reviewReplyText
                            )
                        )
                    },
                    accentTint = NeonEmerald,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить шаблоны отзывов", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = TextWhite)
                }
            }
        }

        // 3. Orders List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Заказы (${orders.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite
                )
                PureGlassButton(
                    onClick = {
                        onCreateTestOrder("Buyer_${(10..99).random()}", "Игровая валюта (Выдано)", 1650.0, 27)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Заказ (27ч)", color = TextWhite, fontSize = 11.sp)
                }
            }
        }

        // 4. Orders Items
        items(orders, key = { it.orderId }) { order ->
            val hoursElapsed = ((System.currentTimeMillis() - order.createdAtTimestamp) / 3600_000L).coerceAtLeast(1L)
            val isOverdue = order.status == "PAID" && hoursElapsed >= settings.unconfirmedThresholdHours

            DarkNeonGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${order.orderId} • ${order.buyerName}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = TextWhite,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    DarkNeonStatusBadge(
                        text = when {
                            order.status == "CLOSED" -> "ЗАКРЫТ"
                            order.isSupportTicketSent -> "ТИКЕТ: ${order.supportTicketId}"
                            isOverdue -> "ОЖИДАЕТ ${hoursElapsed}Ч"
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
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = order.lotDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextLight
                )
                Text(
                    text = "Сумма: ${order.priceRub.toInt()} ₽",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.5.sp,
                    color = NeonCyan
                )

                if (order.status == "PAID") {
                    Spacer(modifier = Modifier.height(7.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!order.isSupportTicketSent) {
                            PureGlassButton(
                                onClick = { onSendTicketToTp(order) },
                                accentTint = NeonAmber,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Автотикет в ТП", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = TextWhite)
                            }
                        }
                        PureGlassButton(
                            onClick = { onConfirmOrderAndReview(order) },
                            accentTint = NeonEmerald,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Подтвердить + Отзыв", fontSize = 11.sp, color = TextWhite)
                        }
                    }
                }
            }
        }
    }
}
