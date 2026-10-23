package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AutoReplyRuleEntity
import com.example.data.AutomationSettingsEntity
import com.example.data.FunPayAccountEntity
import com.example.data.FunPayOrderEntity
import com.example.data.LotCategoryEntity
import com.example.data.SessionLogEntity
import com.example.repository.FunPayRepository
import com.example.service.FunPaySessionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FunPayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FunPayRepository

    val accounts: StateFlow<List<FunPayAccountEntity>>
    val lotCategories: StateFlow<List<LotCategoryEntity>>
    val autoReplyRules: StateFlow<List<AutoReplyRuleEntity>>
    val orders: StateFlow<List<FunPayOrderEntity>>
    val settings: StateFlow<AutomationSettingsEntity>
    val logs: StateFlow<List<SessionLogEntity>>

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private val _chatSimulatorReplies = MutableStateFlow<List<String>>(emptyList())
    val chatSimulatorReplies: StateFlow<List<String>> = _chatSimulatorReplies.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = FunPayRepository(db.funPayDao())

        accounts = repository.accountsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        lotCategories = repository.lotCategoriesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        autoReplyRules = repository.autoReplyRulesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        orders = repository.ordersFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        settings = repository.settingsFlow.map { it ?: AutomationSettingsEntity() }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AutomationSettingsEntity()
        )
        logs = repository.logsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.ensureInitialDataSeeded()
        }
    }

    fun dismissBanner() {
        _statusBannerMessage.value = null
    }

    private fun showBanner(msg: String) {
        _statusBannerMessage.value = msg
    }

    // Account actions
    fun connectFunPayAccount(username: String, goldenKey: String, userAgent: String) {
        viewModelScope.launch {
            _isBusy.value = true
            val (ok, msg) = repository.addAndVerifyAccount(username, goldenKey, userAgent)
            _isBusy.value = false
            showBanner(if (ok) "✅ $msg" else "⚠️ $msg")
        }
    }

    fun switchActiveAccount(account: FunPayAccountEntity) {
        viewModelScope.launch {
            repository.selectActiveAccount(account)
            showBanner("Активен аккаунт: ${account.username}")
        }
    }

    fun deleteAccount(account: FunPayAccountEntity) {
        viewModelScope.launch {
            repository.removeAccount(account)
            showBanner("Аккаунт ${account.username} удален")
        }
    }

    // 24/7 Session Service control
    fun toggle24x7SessionServer(enable: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            val updated = current.copy(isSessionLoopRunning = enable)
            repository.updateSettings(updated)
            val app = getApplication<Application>()
            if (enable) {
                FunPaySessionService.startService(app)
                showBanner("🚀 Сервер 24/7 запущен! Автоподнятие и мониторинг ТП работают в фоне")
            } else {
                FunPaySessionService.stopService(app)
                showBanner("⏸ Фоновая сессия 24/7 приостановлена")
            }
        }
    }

    // Lot Raising actions
    fun addLotCategory(
        gameId: String,
        nodeId: String,
        title: String,
        gameName: String,
        lotsCount: Int,
        intervalMinutes: Int
    ) {
        viewModelScope.launch {
            repository.addLotCategory(gameId, nodeId, title, gameName, lotsCount, intervalMinutes)
            showBanner("✅ Добавлен раздел «$title» в очередь автоподнятия")
        }
    }

    fun toggleCategoryRaise(category: LotCategoryEntity, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleLotCategory(category, enabled)
        }
    }

    fun deleteCategory(category: LotCategoryEntity) {
        viewModelScope.launch {
            repository.deleteLotCategory(category)
            showBanner("Раздел «${category.title}» удален")
        }
    }

    fun raiseSingleCategoryNow(category: LotCategoryEntity) {
        viewModelScope.launch {
            _isBusy.value = true
            val msg = repository.raiseSingleCategoryNow(category)
            _isBusy.value = false
            showBanner("🚀 ${category.title}: $msg")
        }
    }

    fun raiseAllCategoriesNow() {
        viewModelScope.launch {
            _isBusy.value = true
            val count = repository.raiseAllEnabledCategories()
            _isBusy.value = false
            showBanner("🚀 Поднято разделов лотов: $count шт.!")
        }
    }

    // Auto-reply & Greeting actions
    fun saveAutoReplyRule(rule: AutoReplyRuleEntity) {
        viewModelScope.launch {
            repository.saveAutoReplyRule(rule)
            showBanner("💬 Правило автоответа сохранено")
        }
    }

    fun toggleAutoReplyRule(rule: AutoReplyRuleEntity, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAutoReplyRule(rule, enabled)
        }
    }

    fun deleteAutoReplyRule(rule: AutoReplyRuleEntity) {
        viewModelScope.launch {
            repository.deleteAutoReplyRule(rule)
            showBanner("Правило удалено")
        }
    }

    fun testIncomingBuyerMessage(
        buyerName: String,
        message: String,
        lotTitle: String,
        isFirstContact: Boolean
    ) {
        viewModelScope.launch {
            val replies = repository.simulateIncomingBuyerChat(
                buyerName = buyerName.ifBlank { "ShadowBuyer" },
                incomingMessage = message,
                lotTitle = lotTitle.ifBlank { "10000 Золота + Бонус" },
                isFirstContact = isFirstContact
            )
            _chatSimulatorReplies.value = replies
            if (replies.isEmpty()) {
                showBanner("ℹ️ Триггеры не совпали. Попробуйте слова из ваших правил")
            } else {
                showBanner("🤖 Отправлено автоответов: ${replies.size}")
            }
        }
    }

    // Orders, Review Request, Review Auto-Reply & 24h Support Ticket actions
    fun confirmOrderWithReviewAutomation(order: FunPayOrderEntity, stars: Int = 5) {
        viewModelScope.launch {
            _isBusy.value = true
            repository.confirmOrderAndTriggerReviewFlow(
                order = order,
                buyerReviewText = "Супер продавец, моментальная выдача и вежливое общение!",
                buyerStars = stars
            )
            _isBusy.value = false
            showBanner("✅ Заказ #${order.orderId} закрыт • Отправлена просьба отзыва и автоответ ($stars★)!")
        }
    }

    fun sendAutoTicketToSupportNow(order: FunPayOrderEntity) {
        viewModelScope.launch {
            _isBusy.value = true
            val ticketId = repository.submitSupportTicketForOrder(order)
            _isBusy.value = false
            showBanner("🛠 Автотикет $ticketId отправлен в ТП FunPay для заказа #${order.orderId}!")
        }
    }

    fun runScanOverdueOrdersForTp() {
        viewModelScope.launch {
            _isBusy.value = true
            val count = repository.scanAndAutoTicketOverdueOrders()
            _isBusy.value = false
            if (count > 0) {
                showBanner("🛠 Автоматически отправлено тикетов в ТП (заказы >24ч): $count шт.!")
            } else {
                showBanner("ℹ️ Все просроченные заказы (>24ч) уже имеют открытые тикеты в ТП")
            }
        }
    }

    fun createSampleOrder(buyerName: String, lotDescription: String, priceRub: Double, hoursAgo: Int) {
        viewModelScope.launch {
            repository.addSimulatedOrder(buyerName, lotDescription, priceRub, hoursAgo)
            showBanner("🛒 Добавлен заказ от $buyerName (${hoursAgo}ч назад)")
        }
    }

    // Settings & Telegram actions
    fun saveAutomationSettings(updated: AutomationSettingsEntity, showToast: Boolean = true) {
        viewModelScope.launch {
            repository.updateSettings(updated)
            if (showToast) {
                showBanner("✅ Настройки автоматизации сохранены")
            }
        }
    }

    fun testTelegramNotification(botToken: String, chatId: String) {
        viewModelScope.launch {
            _isBusy.value = true
            val updated = settings.value.copy(
                isTelegramEnabled = true,
                telegramBotToken = botToken.trim(),
                telegramChatId = chatId.trim()
            )
            repository.updateSettings(updated)
            val (ok, msg) = repository.testTelegramConnection(botToken, chatId)
            _isBusy.value = false
            showBanner(if (ok) "✈️ Telegram: $msg" else "⚠️ $msg")
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearAllLogs()
            showBanner("Журнал сессии очищен")
        }
    }

    fun previewTemplate(template: String): String {
        return repository.formatTemplate(template)
    }
}
