package com.carsharehub.notification

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TelegramNotificationSchedulerImpl(
    private val notificationService: NotificationService
) : NotificationScheduler {

    private val logger = KotlinLogging.logger {}

    @Value("\${telegram.bot.enabled}")
    private var telegramEnabled: Boolean = false

    @Scheduled(cron = "\${notification.scheduler.overdue-cron:0 0 9 * * *}")
    override fun scheduleOverdueNotifications() {
        if (!telegramEnabled) {
            logger.debug { "Telegram bot is disabled. Skipping overdue notifications" }
            return
        }
        logger.info { "Scheduling overdue notifications" }
        // TODO: Implement logic to find overdue rentals and send notifications
    }

    @Scheduled(cron = "\${notification.scheduler.daily-cron:0 0 8 * * *}")
    override fun scheduleDailyReminders() {
        if (!telegramEnabled) {
            logger.debug { "Telegram bot is disabled. Skipping daily reminders" }
            return
        }
        logger.info { "Scheduling daily reminders" }
        // TODO: Implement logic to send daily reminders to active users
    }

    @Scheduled(cron = "\${notification.scheduler.payment-cron:0 0 10 * * *}")
    override fun schedulePaymentReminders() {
        if (!telegramEnabled) {
            logger.debug { "Telegram bot is disabled. Skipping payment reminders" }
            return
        }
        logger.info { "Scheduling payment reminders" }
        // TODO: Implement logic to find pending payments and send reminders
    }
}
