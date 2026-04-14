package com.carsharehub.notification

interface NotificationScheduler {
    fun scheduleOverdueNotifications()
    fun scheduleDailyReminders()
    fun schedulePaymentReminders()
}
