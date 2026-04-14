package com.carsharehub.notification

import com.carsharehub.rental.RentalRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TelegramNotificationSchedulerImpl(
    private val rentalRepository: RentalRepository,
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

        val activeRentals = rentalRepository.findAllDetailedActiveRentalsWithTelegramChatId()

        for (rental in activeRentals) {
            val user = rental.user ?: continue
            val returnDate = rental.returnDate ?: continue
            val userId = user.id ?: continue

            if (returnDate.isBefore(LocalDate.now()) || returnDate.isEqual(LocalDate.now())) {
                val message = "${user.firstName}, return date is today or earlier, " +
                    "and the car is still not returned"
                notificationService.sendNotification(userId, message)
            } else if (returnDate.minusDays(1) == LocalDate.now()) {
                val message = "${user.firstName}, return date is tomorrow, " +
                    "please remember to return the car"
                notificationService.sendNotification(userId, message)
            } else {
                val message = "${user.firstName}, no rentals due today"
                notificationService.sendNotification(userId, message)
            }
        }
    }

    @Scheduled(cron = "\${notification.scheduler.daily-cron:0 0 8 * * *}")
    override fun scheduleDailyReminders() {
        logger.debug { "Daily reminders not implemented" }
    }

    @Scheduled(cron = "\${notification.scheduler.payment-cron:0 0 10 * * *}")
    override fun schedulePaymentReminders() {
        logger.debug { "Payment reminders not implemented" }
    }
}
