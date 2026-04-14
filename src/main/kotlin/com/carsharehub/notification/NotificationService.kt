package com.carsharehub.notification

interface NotificationService {
    fun sendNotification(userId: Long, message: String)
    fun sendNotificationToAll(message: String)
}
