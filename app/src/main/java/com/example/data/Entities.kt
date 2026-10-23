package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "funpay_accounts")
data class FunPayAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val userId: String,
    val goldenKey: String,
    val userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36",
    val csrfToken: String = "",
    val balanceRub: Double = 0.0,
    val activeOrdersCount: Int = 0,
    val isActive: Boolean = true,
    val lastSyncTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "lot_categories")
data class LotCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameId: String,
    val nodeId: String,
    val title: String,
    val gameName: String,
    val lotsCount: Int = 5,
    val isAutoRaiseEnabled: Boolean = true,
    val intervalMinutes: Int = 240, // 4 hours default FunPay cooldown, or custom check interval
    val lastRaisedAt: Long = 0L,
    val nextRaiseAt: Long = System.currentTimeMillis() + 15 * 60 * 1000L,
    val totalRaisesCount: Int = 0,
    val lastStatusMessage: String = "Ожидание цикла поднятия"
)

@Entity(tableName = "auto_reply_rules")
data class AutoReplyRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val triggerKeywords: String, // e.g., "!наличие, тут?, актуально"
    val responseTemplate: String,
    val isExactMatch: Boolean = false,
    val isEnabled: Boolean = true,
    val triggeredCount: Int = 0
)

@Entity(tableName = "funpay_orders")
data class FunPayOrderEntity(
    @PrimaryKey val orderId: String, // e.g., "#FP8941205"
    val buyerName: String,
    val lotDescription: String,
    val priceRub: Double,
    val status: String, // "PAID" (Оплачен - ждет подтверждения), "CLOSED" (Закрыт), "REFUNDED" (Возврат)
    val createdAtTimestamp: Long, // When order was created/paid
    val hasRequestedReview: Boolean = false,
    val hasRepliedToReview: Boolean = false,
    val reviewText: String = "",
    val reviewStars: Int = 5,
    val isSupportTicketSent: Boolean = false,
    val supportTicketSentAt: Long = 0L,
    val supportTicketId: String = ""
)

@Entity(tableName = "automation_settings")
data class AutomationSettingsEntity(
    @PrimaryKey val id: Int = 1,
    // Global modules
    val isSessionLoopRunning: Boolean = false,
    val isAutoRaiseMasterEnabled: Boolean = true,
    val isEternalOnlineEnabled: Boolean = true,
    // Greeting settings
    val isGreetingEnabled: Boolean = true,
    val greetingOnlyNewBuyers: Boolean = true,
    val greetingTemplate: String = "👋 Привет, {buyer}! Спасибо за обращение. Я онлайн и готов помочь по лоту «{lot}». Задавай любой вопрос или оформляй заказ — выдача моментальная!",
    // Auto-reply to reviews settings
    val isAutoReviewReplyEnabled: Boolean = true,
    val minStarsForAutoReviewReply: Int = 4,
    val reviewReplyTemplate: String = "💎 Спасибо большое за покупку и отличный отзыв ({stars}★), {buyer}! Заглядывайте еще, постоянным клиентам скидки и бонусы!",
    // Request review after order confirmation
    val isAskReviewAfterConfirmEnabled: Boolean = true,
    val askReviewDelayMinutes: Int = 2,
    val askReviewTemplate: String = "✅ Заказ #{order_id} успешно выполнен и подтвержден! Буду очень благодарен, если оставите пару слов в отзыве под заказом. Приятной игры, {buyer}!",
    // Auto-ticket to FunPay Support for unconfirmed orders > 24h
    val isAutoTicketTpEnabled: Boolean = true,
    val unconfirmedThresholdHours: Int = 24,
    val autoTicketTpTemplate: String = "Здравствуйте, уважаемая поддержка FunPay! Прошу подтвердить выполненный заказ #{order_id} (покупатель {buyer}, сумма {price} ₽). Все обязательства по заказу полностью выполнены более {hours_wait} ч. назад, покупатель получил товар/услугу, претензий нет, но забыл нажать кнопку подтверждения.",
    // Telegram integration
    val isTelegramEnabled: Boolean = false,
    val telegramBotToken: String = "",
    val telegramChatId: String = "",
    val notifyOnLotRaise: Boolean = true,
    val notifyOnNewOrder: Boolean = true,
    val notifyOnOrderConfirmed: Boolean = true,
    val notifyOnAutoReply: Boolean = true,
    val notifyOnAutoTicketTp: Boolean = true,
    val notifyOnSessionErrors: Boolean = true,
    // Old device 24/7 Server Mode
    val isKeepScreenAwake: Boolean = true,
    val isOledDimMode: Boolean = false,
    val autoReconnectOnNetworkDrop: Boolean = true
)

@Entity(tableName = "session_logs")
data class SessionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String, // "RAISE", "CHAT", "REVIEW", "TICKET_TP", "TELEGRAM", "ACCOUNT", "SERVER"
    val title: String,
    val details: String,
    val isSuccess: Boolean = true
)
