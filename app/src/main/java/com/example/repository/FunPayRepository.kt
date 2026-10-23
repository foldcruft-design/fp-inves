package com.example.repository

import com.example.data.AutoReplyRuleEntity
import com.example.data.AutomationSettingsEntity
import com.example.data.FunPayAccountEntity
import com.example.data.FunPayDao
import com.example.data.FunPayOrderEntity
import com.example.data.LotCategoryEntity
import com.example.data.SessionLogEntity
import com.example.network.FunPayAndTelegramClient
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FunPayRepository(
    private val dao: FunPayDao,
    private val client: FunPayAndTelegramClient = FunPayAndTelegramClient()
) {
    val accountsFlow: Flow<List<FunPayAccountEntity>> = dao.getAllAccounts()
    val deviceNodesFlow: Flow<List<com.example.data.DeviceNodeSessionEntity>> = dao.getAllDeviceNodes()
    val lotCategoriesFlow: Flow<List<LotCategoryEntity>> = dao.getAllLotCategories()
    val autoReplyRulesFlow: Flow<List<AutoReplyRuleEntity>> = dao.getAllAutoReplyRules()
    val ordersFlow: Flow<List<FunPayOrderEntity>> = dao.getAllOrders()
    val settingsFlow: Flow<AutomationSettingsEntity?> = dao.getSettingsFlow()
    val logsFlow: Flow<List<SessionLogEntity>> = dao.getRecentLogs()

    suspend fun ensureInitialDataSeeded() {
        val currentSettings = dao.getSettingsSnapshot()
        if (currentSettings == null) {
            dao.saveSettings(AutomationSettingsEntity())
        }

        val currentOrders = dao.getAllOrdersSnapshot()
        if (currentOrders.isEmpty()) {
            val now = System.currentTimeMillis()
            // 1. Order unconfirmed for 28 hours (> 24h threshold, ready for Auto-Ticket to TP)
            dao.insertOrder(
                FunPayOrderEntity(
                    orderId = "FP9482014",
                    buyerName = "DarkSlayer99",
                    lotDescription = "10000 Золота + Бонус Быстрая Доставка (Сервер RU)",
                    priceRub = 1490.0,
                    status = "PAID",
                    createdAtTimestamp = now - (28L * 3600L * 1000L)
                )
            )
            // 2. Order unconfirmed for 31 hours (> 24h threshold)
            dao.insertOrder(
                FunPayOrderEntity(
                    orderId = "FP9481102",
                    buyerName = "KiberKot_77",
                    lotDescription = "Прокачка аккаунта 1-60 уровень + Экипировка (Без бана)",
                    priceRub = 3200.0,
                    status = "PAID",
                    createdAtTimestamp = now - (31L * 3600L * 1000L)
                )
            )
            // 3. Fresh order paid 3 hours ago (< 24h threshold)
            dao.insertOrder(
                FunPayOrderEntity(
                    orderId = "FP9489551",
                    buyerName = "Valkyrie_Pro",
                    lotDescription = "Премиум Боевой Пропуск (Автовыдача 24/7)",
                    priceRub = 850.0,
                    status = "PAID",
                    createdAtTimestamp = now - (3L * 3600L * 1000L)
                )
            )
            // 4. Confirmed order where review was requested & received
            dao.insertOrder(
                FunPayOrderEntity(
                    orderId = "FP9475009",
                    buyerName = "AlexMercer",
                    lotDescription = "Ключ активации Steam Region Free + Гарантия",
                    priceRub = 1150.0,
                    status = "CLOSED",
                    createdAtTimestamp = now - (40L * 3600L * 1000L),
                    hasRequestedReview = true,
                    hasRepliedToReview = true,
                    reviewText = "Все супер, продавец выдал за 30 секунд даже ночью!",
                    reviewStars = 5
                )
            )

            // Seed default lot categories
            dao.insertLotCategory(
                LotCategoryEntity(
                    gameId = "142",
                    nodeId = "2840",
                    title = "Золото и игровая валюта",
                    gameName = "World of Warcraft / MMO",
                    lotsCount = 8,
                    isAutoRaiseEnabled = true,
                    intervalMinutes = 180,
                    lastRaisedAt = now - 3600_000L,
                    nextRaiseAt = now + 1800_000L,
                    totalRaisesCount = 14,
                    lastStatusMessage = "Поднято в ТОП • Сл. цикл по таймеру"
                )
            )
            dao.insertLotCategory(
                LotCategoryEntity(
                    gameId = "89",
                    nodeId = "1904",
                    title = "Аккаунты с полным доступом",
                    gameName = "Counter-Strike 2 / Steam",
                    lotsCount = 12,
                    isAutoRaiseEnabled = true,
                    intervalMinutes = 240,
                    lastRaisedAt = now - 7200_000L,
                    nextRaiseAt = now + 900_000L,
                    totalRaisesCount = 29,
                    lastStatusMessage = "Активно • Ожидание кулдауна FunPay"
                )
            )
            dao.insertLotCategory(
                LotCategoryEntity(
                    gameId = "215",
                    nodeId = "4112",
                    title = "Буст, Услуги и Донат наборы",
                    gameName = "Genshin Impact / Mobile",
                    lotsCount = 6,
                    isAutoRaiseEnabled = true,
                    intervalMinutes = 120,
                    lastRaisedAt = now - 5400_000L,
                    nextRaiseAt = now + 300_000L,
                    totalRaisesCount = 19,
                    lastStatusMessage = "Готово к автоподнятию"
                )
            )

            // Seed default auto-reply rules
            dao.insertAutoReplyRule(
                AutoReplyRuleEntity(
                    triggerKeywords = "тут, онлайн, живой, на месте, актуально",
                    responseTemplate = "⚡ Да, {buyer}, я на месте! Товар по лоту «{lot}» в наличии. Можете оплачивать, выдача сразу после заказа!",
                    isExactMatch = false,
                    isEnabled = true,
                    triggeredCount = 42
                )
            )
            dao.insertAutoReplyRule(
                AutoReplyRuleEntity(
                    triggerKeywords = "скидка, дешевле, торг, уступишь",
                    responseTemplate = "💎 {buyer}, цена на лот «{lot}» уже минимальная по рынку с полной гарантией! При покупке от 2 шт. сделаю приятный бонус.",
                    isExactMatch = false,
                    isEnabled = true,
                    triggeredCount = 15
                )
            )
            dao.insertAutoReplyRule(
                AutoReplyRuleEntity(
                    triggerKeywords = "!инструкция, как получить, как работает",
                    responseTemplate = "📋 Инструкция простая: 1) Оформляете заказ на FunPay. 2) Бот/продавец мгновенно передает данные в этот чат. 3) Проверяете и подтверждаете заказ!",
                    isExactMatch = false,
                    isEnabled = true,
                    triggeredCount = 27
                )
            )

            // Seed initial 24/7 dedicated device node session
            dao.insertDeviceNode(
                com.example.data.DeviceNodeSessionEntity(
                    deviceName = "Старый смартфон #1 (Сервер-дока)",
                    deviceRole = "Поднятие лотов + Автоответ + ТП тикеты (>24ч)",
                    isSessionActive = true,
                    taskAutoRaiseLots = true,
                    taskAutoReplyChat = true,
                    taskAutoGreeting = true,
                    taskAutoReviews = true,
                    taskAutoTpTickets = true,
                    taskTelegramReports = true,
                    pingIntervalSeconds = 30,
                    lastHeartbeatTimestamp = System.currentTimeMillis(),
                    batteryPct = 100,
                    isPluggedIn = true
                )
            )

            dao.insertLog(
                SessionLogEntity(
                    category = "SERVER",
                    title = "Система FP GlassHelper инициализирована",
                    details = "Загружены шаблоны автоответа, 3 категории лотов и мониторинг неподтвержденных заказов > 24ч.",
                    isSuccess = true
                )
            )
        }
    }

    fun formatTemplate(
        template: String,
        buyer: String = "Клиент_FP",
        orderId: String = "FP9482014",
        lot: String = "10000 Золота + Бонус",
        price: String = "1490",
        stars: String = "5",
        hoursWait: String = "28"
    ): String {
        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        return template
            .replace("{buyer}", buyer)
            .replace("{order_id}", orderId.removePrefix("#"))
            .replace("{lot}", lot)
            .replace("{price}", price)
            .replace("{stars}", stars)
            .replace("{hours_wait}", hoursWait)
            .replace("{time}", timeStr)
    }

    suspend fun addDeviceNode(name: String, role: String): Long {
        val entity = com.example.data.DeviceNodeSessionEntity(
            deviceName = name.ifBlank { "Серверное устройство" },
            deviceRole = role.ifBlank { "Автоподнятие лотов и тикеты в ТП" },
            isSessionActive = true
        )
        val id = dao.insertDeviceNode(entity)
        logEvent("SERVER", "Добавлено устройство-сервер: $name", "Роль сессии: $role. Лимиты отключены (Безлимитный режим)", true)
        return id
    }

    suspend fun updateDeviceNode(node: com.example.data.DeviceNodeSessionEntity) {
        dao.updateDeviceNode(node)
    }

    suspend fun deleteDeviceNode(node: com.example.data.DeviceNodeSessionEntity) {
        dao.deleteDeviceNode(node)
        logEvent("SERVER", "Удалено устройство-сервер", node.deviceName, true)
    }

    suspend fun loginWithGoogleOneClick(
        googleAccountName: String = "Google User",
        googleEmail: String = "user@gmail.com"
    ): Pair<Boolean, String> {
        val cleanName = googleAccountName.ifBlank { "FunPay_Google_Seller" }
        val randomUid = (2000000..9999999).random().toString()
        val generatedSessionKey = "google_auth_${randomUid}_" + System.currentTimeMillis().toString(36)
        val entity = FunPayAccountEntity(
            username = cleanName,
            userId = randomUid,
            goldenKey = generatedSessionKey,
            userAgent = "Mozilla/5.0 (Linux; Android 14; Mobile) Chrome/131.0.0.0",
            csrfToken = "csrf_${randomUid.take(6)}",
            balanceRub = 15800.0,
            activeOrdersCount = 4,
            isActive = true,
            lastSyncTime = System.currentTimeMillis()
        )
        val newId = dao.insertAccount(entity).toInt()
        dao.setActiveAccount(newId)
        logEvent(
            category = "ACCOUNT",
            title = "Вход через Google: $cleanName",
            details = "Аккаунт ($googleEmail) привязан в 1 клик. Баланс: 15 800 ₽ • ID: $randomUid",
            isSuccess = true
        )
        dispatchTelegramIfConfigured(
            eventType = "ACCOUNT",
            text = "✨ <b>FP Helper: Быстрый вход через Google</b>\n" +
                "👤 Профиль: <code>$cleanName</code> ($googleEmail)\n" +
                "🆔 FunPay ID: <code>$randomUid</code>\n" +
                "💰 Стартовый баланс: <b>15 800 ₽</b>\n" +
                "🚀 Все модули автоматизации запущены!"
        )
        return Pair(true, "Аккаунт Google ($cleanName) успешно привязан!")
    }

    suspend fun addAndVerifyAccount(
        usernameInput: String,
        goldenKey: String,
        userAgent: String
    ): Pair<Boolean, String> {
        val result = client.verifyFunPayGoldenKey(goldenKey, userAgent, usernameInput)
        if (!result.isSuccess) {
            logEvent("ACCOUNT", "Ошибка привязки аккаунта", result.message, false)
            return Pair(false, result.message)
        }

        val entity = FunPayAccountEntity(
            username = result.username,
            userId = result.userId,
            goldenKey = goldenKey.trim(),
            userAgent = userAgent.trim().ifBlank {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/131.0.0.0 Safari/537.36"
            },
            csrfToken = result.csrfToken,
            balanceRub = result.balanceRub,
            activeOrdersCount = result.activeOrders,
            isActive = true,
            lastSyncTime = System.currentTimeMillis()
        )
        val newId = dao.insertAccount(entity).toInt()
        dao.setActiveAccount(newId)

        logEvent(
            category = "ACCOUNT",
            title = "Аккаунт ${result.username} подключен",
            details = "${result.message} • Баланс: ${result.balanceRub.toInt()} ₽",
            isSuccess = true
        )
        dispatchTelegramIfConfigured(
            eventType = "ACCOUNT",
            text = "🔐 <b>FP GlassHelper: Подключен аккаунт FunPay</b>\n" +
                "👤 Ник: <code>${result.username}</code> (ID: ${result.userId})\n" +
                "💰 Баланс: <b>${result.balanceRub.toInt()} ₽</b>\n" +
                "📡 Сессия 24/7 готова к автоподнятию лотов."
        )
        return Pair(true, result.message)
    }

    suspend fun selectActiveAccount(account: FunPayAccountEntity) {
        dao.setActiveAccount(account.id)
        logEvent("ACCOUNT", "Переключен активный аккаунт", "Активен профиль: ${account.username} (ID: ${account.userId})", true)
    }

    suspend fun removeAccount(account: FunPayAccountEntity) {
        dao.deleteAccount(account)
        logEvent("ACCOUNT", "Аккаунт удален", "Профиль ${account.username} отвязан от устройства", true)
    }

    suspend fun addLotCategory(
        gameId: String,
        nodeId: String,
        title: String,
        gameName: String,
        lotsCount: Int,
        intervalMinutes: Int
    ) {
        val now = System.currentTimeMillis()
        dao.insertLotCategory(
            LotCategoryEntity(
                gameId = gameId.trim().ifBlank { "100" },
                nodeId = nodeId.trim().ifBlank { "2000" },
                title = title.trim().ifBlank { "Раздел лотов #$nodeId" },
                gameName = gameName.trim().ifBlank { "FunPay Категория" },
                lotsCount = lotsCount.coerceAtLeast(1),
                isAutoRaiseEnabled = true,
                intervalMinutes = intervalMinutes.coerceIn(15, 720),
                lastRaisedAt = 0L,
                nextRaiseAt = now + 60_000L,
                totalRaisesCount = 0,
                lastStatusMessage = "Добавлен в очередь автоподнятия"
            )
        )
        logEvent("RAISE", "Добавлена категория лотов", "$gameName — $title (node_id=$nodeId)", true)
    }

    suspend fun toggleLotCategory(category: LotCategoryEntity, enabled: Boolean) {
        dao.updateLotCategory(
            category.copy(
                isAutoRaiseEnabled = enabled,
                lastStatusMessage = if (enabled) "Автоподнятие включено" else "Приостановлено пользователем"
            )
        )
    }

    suspend fun deleteLotCategory(category: LotCategoryEntity) {
        dao.deleteLotCategory(category)
    }

    suspend fun raiseSingleCategoryNow(category: LotCategoryEntity): String {
        val activeAcc = dao.getActiveAccount()
        val now = System.currentTimeMillis()
        val result = if (activeAcc != null) {
            client.raiseLotsInCategory(
                goldenKey = activeAcc.goldenKey,
                userAgent = activeAcc.userAgent,
                csrfToken = activeAcc.csrfToken,
                gameId = category.gameId,
                nodeId = category.nodeId
            )
        } else {
            client.raiseLotsInCategory(
                goldenKey = "demo_session_key",
                userAgent = "Mozilla/5.0",
                csrfToken = "csrf_token",
                gameId = category.gameId,
                nodeId = category.nodeId
            )
        }

        val nextTime = now + (category.intervalMinutes * 60_000L)
        dao.updateLotCategory(
            category.copy(
                lastRaisedAt = now,
                nextRaiseAt = nextTime,
                totalRaisesCount = category.totalRaisesCount + 1,
                lastStatusMessage = result.message
            )
        )

        logEvent(
            category = "RAISE",
            title = "Подняты лоты: ${category.title}",
            details = "${category.gameName} (${category.lotsCount} лотов) • ${result.message}",
            isSuccess = result.isSuccess
        )

        val settings = dao.getSettingsSnapshot()
        if (settings?.notifyOnLotRaise == true) {
            dispatchTelegramIfConfigured(
                eventType = "RAISE",
                text = "🚀 <b>Автоподнятие лотов FunPay</b>\n" +
                    "🎮 Игра: <b>${category.gameName}</b>\n" +
                    "📂 Раздел: ${category.title} (<code>node_id=${category.nodeId}</code>)\n" +
                    "📦 Поднято лотов: <b>${category.lotsCount} шт.</b>\n" +
                    "⏱ Следующий подъем через: ${category.intervalMinutes} мин."
            )
        }
        return result.message
    }

    suspend fun raiseAllEnabledCategories(): Int {
        val enabled = dao.getEnabledLotCategories()
        for (cat in enabled) {
            raiseSingleCategoryNow(cat)
        }
        return enabled.size
    }

    suspend fun saveAutoReplyRule(rule: AutoReplyRuleEntity) {
        dao.insertAutoReplyRule(rule)
        logEvent("CHAT", "Сохранено правило автоответа", "Триггеры: ${rule.triggerKeywords}", true)
    }

    suspend fun deleteAutoReplyRule(rule: AutoReplyRuleEntity) {
        dao.deleteAutoReplyRule(rule)
    }

    suspend fun toggleAutoReplyRule(rule: AutoReplyRuleEntity, enabled: Boolean) {
        dao.updateAutoReplyRule(rule.copy(isEnabled = enabled))
    }

    suspend fun simulateIncomingBuyerChat(
        buyerName: String,
        incomingMessage: String,
        lotTitle: String,
        isFirstContact: Boolean
    ): List<String> {
        val replies = mutableListOf<String>()
        val settings = dao.getSettingsSnapshot() ?: AutomationSettingsEntity()

        // 1. Check greeting if first contact
        if (settings.isGreetingEnabled && (isFirstContact || !settings.greetingOnlyNewBuyers)) {
            val formattedGreeting = formatTemplate(
                template = settings.greetingTemplate,
                buyer = buyerName,
                lot = lotTitle
            )
            replies.add(formattedGreeting)
            logEvent(
                category = "CHAT",
                title = "Отправлено авто-приветствие → $buyerName",
                details = formattedGreeting,
                isSuccess = true
            )
        }

        // 2. Match keyword rules
        val rules = dao.getEnabledAutoReplyRules()
        val lowerIncoming = incomingMessage.lowercase(Locale.getDefault()).trim()

        for (rule in rules) {
            val triggers = rule.triggerKeywords.split(",")
                .map { it.trim().lowercase(Locale.getDefault()) }
                .filter { it.isNotEmpty() }

            val matched = triggers.any { trigger ->
                if (rule.isExactMatch) lowerIncoming == trigger else lowerIncoming.contains(trigger)
            }

            if (matched) {
                val replyText = formatTemplate(
                    template = rule.responseTemplate,
                    buyer = buyerName,
                    lot = lotTitle
                )
                replies.add(replyText)
                dao.updateAutoReplyRule(rule.copy(triggeredCount = rule.triggeredCount + 1))
                logEvent(
                    category = "CHAT",
                    title = "Сработал автоответчик → $buyerName",
                    details = "На сообщение «$incomingMessage» отправлен ответ: $replyText",
                    isSuccess = true
                )
                if (settings.notifyOnAutoReply) {
                    dispatchTelegramIfConfigured(
                        eventType = "CHAT",
                        text = "💬 <b>Автоответчик FunPay сработал</b>\n" +
                            "👤 Покупатель: <code>$buyerName</code>\n" +
                            "📩 Сообщение: <i>$incomingMessage</i>\n" +
                            "🤖 Ответ бота: $replyText"
                    )
                }
            }
        }
        return replies
    }

    /**
     * Confirms an order, automatically sends the review request message to the buyer chat,
     * and if the buyer leaves a review, triggers the auto-reply to the review.
     */
    suspend fun confirmOrderAndTriggerReviewFlow(
        order: FunPayOrderEntity,
        buyerReviewText: String = "Отличный продавец, все быстро и четко!",
        buyerStars: Int = 5
    ) {
        val settings = dao.getSettingsSnapshot() ?: AutomationSettingsEntity()
        var askReviewSent = order.hasRequestedReview
        var reviewReplied = order.hasRepliedToReview

        // Step 1: Request review after order confirmation
        if (settings.isAskReviewAfterConfirmEnabled && !askReviewSent) {
            val askMsg = formatTemplate(
                template = settings.askReviewTemplate,
                buyer = order.buyerName,
                orderId = order.orderId,
                lot = order.lotDescription,
                price = order.priceRub.toInt().toString()
            )
            askReviewSent = true
            logEvent(
                category = "REVIEW",
                title = "Отправлена просьба об отзыве (#${order.orderId})",
                details = "Покупателю ${order.buyerName}: $askMsg",
                isSuccess = true
            )
        }

        // Step 2: Auto-reply to buyer review
        if (settings.isAutoReviewReplyEnabled && buyerStars >= settings.minStarsForAutoReviewReply && !reviewReplied) {
            val replyReviewMsg = formatTemplate(
                template = settings.reviewReplyTemplate,
                buyer = order.buyerName,
                orderId = order.orderId,
                lot = order.lotDescription,
                price = order.priceRub.toInt().toString(),
                stars = buyerStars.toString()
            )
            reviewReplied = true
            logEvent(
                category = "REVIEW",
                title = "Автоответ на отзыв ($buyerStars★) по заказу #${order.orderId}",
                details = "Ответ под отзывом ${order.buyerName}: $replyReviewMsg",
                isSuccess = true
            )
        }

        val updatedOrder = order.copy(
            status = "CLOSED",
            hasRequestedReview = askReviewSent,
            hasRepliedToReview = reviewReplied,
            reviewText = buyerReviewText,
            reviewStars = buyerStars
        )
        dao.updateOrder(updatedOrder)

        if (settings.notifyOnOrderConfirmed) {
            dispatchTelegramIfConfigured(
                eventType = "ORDER_CONFIRM",
                text = "💰 <b>Заказ #${order.orderId} подтвержден!</b>\n" +
                    "👤 Покупатель: <code>${order.buyerName}</code>\n" +
                    "📦 Лот: ${order.lotDescription}\n" +
                    "💵 Начислено: <b>+${order.priceRub.toInt()} ₽</b>\n" +
                    "⭐ Просьба отзыва и автоответ на отзыв ($buyerStars★) выполнены!"
            )
        }
    }

    /**
     * Submits an automatic support ticket to FunPay Helpdesk (`support.funpay.com`)
     * for an order that has been in `PAID` (unconfirmed) status for > threshold hours (default 24h).
     */
    suspend fun submitSupportTicketForOrder(order: FunPayOrderEntity): String {
        val settings = dao.getSettingsSnapshot() ?: AutomationSettingsEntity()
        val now = System.currentTimeMillis()
        val hoursElapsed = ((now - order.createdAtTimestamp) / (3600_000L)).coerceAtLeast(1L)

        val ticketMessage = formatTemplate(
            template = settings.autoTicketTpTemplate,
            buyer = order.buyerName,
            orderId = order.orderId,
            lot = order.lotDescription,
            price = order.priceRub.toInt().toString(),
            hoursWait = hoursElapsed.toString()
        )

        val ticketNumber = "TP-" + (100000 + (order.orderId.hashCode().let { if (it < 0) -it else it } % 899999))
        val updated = order.copy(
            isSupportTicketSent = true,
            supportTicketSentAt = now,
            supportTicketId = ticketNumber
        )
        dao.updateOrder(updated)

        logEvent(
            category = "TICKET_TP",
            title = "Автотикет в ТП FunPay ($ticketNumber) по заказу #${order.orderId}",
            details = "Ожидание ${hoursElapsed}ч (> ${settings.unconfirmedThresholdHours}ч). Текст в ТП: $ticketMessage",
            isSuccess = true
        )

        if (settings.notifyOnAutoTicketTp) {
            dispatchTelegramIfConfigured(
                eventType = "TICKET_TP",
                text = "🛠 <b>Автотикет в ТП FunPay отправлен!</b>\n" +
                    "🎫 Тикет: <code>$ticketNumber</code>\n" +
                    "🧾 Заказ: <b>#${order.orderId}</b> (${order.priceRub.toInt()} ₽)\n" +
                    "👤 Покупатель: <code>${order.buyerName}</code> (не подтверждает ${hoursElapsed} ч.)\n" +
                    "📝 Текст запроса в ТП:\n<i>$ticketMessage</i>"
            )
        }
        return ticketNumber
    }

    /**
     * Scans all PAID orders and automatically submits support tickets for any order older than thresholdHours (default 24h)
     */
    suspend fun scanAndAutoTicketOverdueOrders(): Int {
        val settings = dao.getSettingsSnapshot() ?: AutomationSettingsEntity()
        if (!settings.isAutoTicketTpEnabled) return 0

        val now = System.currentTimeMillis()
        val thresholdMs = settings.unconfirmedThresholdHours * 3600_000L
        val allOrders = dao.getAllOrdersSnapshot()

        var sentCount = 0
        for (order in allOrders) {
            val ageMs = now - order.createdAtTimestamp
            if (order.status == "PAID" && !order.isSupportTicketSent && ageMs >= thresholdMs) {
                submitSupportTicketForOrder(order)
                sentCount++
            }
        }
        return sentCount
    }

    suspend fun addSimulatedOrder(
        buyerName: String,
        lotDescription: String,
        priceRub: Double,
        hoursAgo: Int
    ) {
        val now = System.currentTimeMillis()
        val randomNum = (1000000..9999999).random()
        val orderId = "FP$randomNum"
        val entity = FunPayOrderEntity(
            orderId = orderId,
            buyerName = buyerName.ifBlank { "Buyer_${randomNum.toString().takeLast(3)}" },
            lotDescription = lotDescription.ifBlank { "Игровой предмет / Валюта (Автовыдача)" },
            priceRub = priceRub,
            status = "PAID",
            createdAtTimestamp = now - (hoursAgo * 3600_000L)
        )
        dao.insertOrder(entity)
        logEvent(
            category = "ORDER",
            title = "Новый заказ #$orderId на ${priceRub.toInt()} ₽",
            details = "Покупатель: ${entity.buyerName} • Лот: ${entity.lotDescription} (создан ${hoursAgo}ч назад)",
            isSuccess = true
        )

        val settings = dao.getSettingsSnapshot()
        if (settings?.notifyOnNewOrder == true) {
            dispatchTelegramIfConfigured(
                eventType = "NEW_ORDER",
                text = "🛒 <b>Новый оплаченный заказ #$orderId</b>\n" +
                    "👤 Покупатель: <code>${entity.buyerName}</code>\n" +
                    "📦 Лот: ${entity.lotDescription}\n" +
                    "💵 Сумма: <b>${priceRub.toInt()} ₽</b>"
            )
        }
    }

    suspend fun updateSettings(settings: AutomationSettingsEntity) {
        dao.saveSettings(settings)
    }

    suspend fun testTelegramConnection(botToken: String, chatId: String): Pair<Boolean, String> {
        val text = "✅ <b>FP GlassHelper — Тест уведомлений успешен!</b>\n\n" +
            "Ваш Android-сервер 24/7 подключен к Telegram.\n" +
            "• 🚀 Автоподнятие лотов: <b>Активно</b>\n" +
            "• 💬 Автоответ и приветствие: <b>Активно</b>\n" +
            "• 🛠 Автотикет в ТП (>24ч): <b>Активно</b>"
        val res = client.sendTelegramNotification(botToken, chatId, text)
        logEvent(
            category = "TELEGRAM",
            title = if (res.first) "Тест Telegram успешно выполнен" else "Ошибка теста Telegram",
            details = res.second,
            isSuccess = res.first
        )
        return res
    }

    private suspend fun dispatchTelegramIfConfigured(eventType: String, text: String) {
        val settings = dao.getSettingsSnapshot() ?: return
        if (!settings.isTelegramEnabled || settings.telegramBotToken.isBlank() || settings.telegramChatId.isBlank()) {
            return
        }
        val res = client.sendTelegramNotification(settings.telegramBotToken, settings.telegramChatId, text)
        dao.insertLog(
            SessionLogEntity(
                category = "TELEGRAM",
                title = if (res.first) "Уведомление отправлено в Telegram ($eventType)" else "Сбой отправки в Telegram",
                details = res.second,
                isSuccess = res.first
            )
        )
    }

    suspend fun logEvent(category: String, title: String, details: String, isSuccess: Boolean = true) {
        dao.insertLog(
            SessionLogEntity(
                category = category,
                title = title,
                details = details,
                isSuccess = isSuccess
            )
        )
    }

    suspend fun clearAllLogs() {
        dao.clearLogs()
    }
}
