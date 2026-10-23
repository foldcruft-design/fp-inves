package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.repository.FunPayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FunPaySessionService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var workerJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var repository: FunPayRepository

    companion object {
        const val CHANNEL_ID = "fp_glass_session_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START_SESSION = "ACTION_START_SESSION"
        const val ACTION_STOP_SESSION = "ACTION_STOP_SESSION"

        fun startService(context: Context) {
            val intent = Intent(context, FunPaySessionService::class.java).apply {
                action = ACTION_START_SESSION
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, FunPaySessionService::class.java).apply {
                action = ACTION_STOP_SESSION
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(applicationContext)
        repository = FunPayRepository(db.funPayDao())
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SESSION -> {
                stopForegroundSession()
                return START_NOT_STICKY
            }
            else -> {
                startForeground(
                    NOTIFICATION_ID,
                    buildNotification("Сессия 24/7 активна • Мониторинг лотов и заказов FunPay")
                )
                acquireWakeLock()
                startAutomationLoop()
            }
        }
        return START_STICKY
    }

    private fun acquireWakeLock() {
        try {
            if (wakeLock == null) {
                val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "FPGlassHelper::SessionWakeLock"
                ).apply {
                    setReferenceCounted(false)
                    acquire(24 * 60 * 60 * 1000L) // Renewed in loop for 24/7 old device server
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun startAutomationLoop() {
        if (workerJob?.isActive == true) return
        workerJob = serviceScope.launch {
            repository.logEvent(
                category = "SERVER",
                title = "Запущен 24/7 Сервер-Сессия на устройстве",
                details = "WakeLock активирован. Фоновое поднятие лотов, проверка неподтвержденных заказов >24ч и Telegram мост работают.",
                isSuccess = true
            )

            var cycleCount = 0
            while (isActive) {
                cycleCount++
                try {
                    val db = AppDatabase.getInstance(applicationContext)
                    val dao = db.funPayDao()
                    val settings = dao.getSettingsSnapshot()

                    if (settings != null) {
                        // 1. Check due lot raises
                        if (settings.isAutoRaiseMasterEnabled) {
                            val now = System.currentTimeMillis()
                            val categories = dao.getEnabledLotCategories()
                            for (cat in categories) {
                                if (now >= cat.nextRaiseAt) {
                                    repository.raiseSingleCategoryNow(cat)
                                }
                            }
                        }

                        // 2. Check overdue orders (>24h) for automatic support ticket
                        if (settings.isAutoTicketTpEnabled) {
                            repository.scanAndAutoTicketOverdueOrders()
                        }
                    }

                    // Update foreground notification heartbeat
                    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.notify(
                        NOTIFICATION_ID,
                        buildNotification("Цикл #$cycleCount • Вечный онлайн и автоподнятие активны")
                    )
                } catch (_: Exception) {
                }

                delay(30_000L) // Heartbeat every 30s
            }
        }
    }

    private fun stopForegroundSession() {
        workerJob?.cancel()
        try {
            wakeLock?.let {
                if (it.isHeld) it.release()
            }
        } catch (_: Exception) {
        }
        serviceScope.launch {
            repository.logEvent(
                category = "SERVER",
                title = "Фоновая сессия 24/7 остановлена",
                details = "Сервер переведен в режим паузы пользователем.",
                isSuccess = true
            )
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(statusText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FP GlassHelper • Сервер 24/7")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        try {
            wakeLock?.let {
                if (it.isHeld) it.release()
            }
        } catch (_: Exception) {
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
