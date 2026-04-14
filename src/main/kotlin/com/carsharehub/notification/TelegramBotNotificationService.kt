package com.carsharehub.notification

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Service
@ConditionalOnProperty(name = ["telegram.bot.enabled"], havingValue = "true")
class TelegramBotNotificationService(
    private val telegramBot: TelegramLongPollingBot,
    private val credentialProvider: TelegramBotCredentialProvider
) : NotificationService {

    private val logger = KotlinLogging.logger {}

    override fun sendNotification(userId: Long, message: String) {
        val chatId = credentialProvider.getChatIdByUserId(userId)
            ?: run {
                logger.warn { "No chat ID found for user $userId" }
                return
            }

        val sendMessage = SendMessage().apply {
            this.chatId = chatId
            this.text = message
        }

        try {
            telegramBot.execute(sendMessage)
            logger.info { "Notification sent to user $userId (chat: $chatId)" }
        } catch (e: TelegramApiException) {
            logger.error(e) { "Failed to send notification to user $userId" }
        }
    }

    override fun sendNotificationToAll(message: String) {
        val chatIds = credentialProvider.getAllChatIds()

        chatIds.forEach { chatId ->
            val sendMessage = SendMessage().apply {
                this.chatId = chatId
                this.text = message
            }

            try {
                telegramBot.execute(sendMessage)
                logger.info { "Broadcast notification sent to chat $chatId" }
            } catch (e: TelegramApiException) {
                logger.error(e) { "Failed to send broadcast notification to chat $chatId" }
            }
        }
    }
}

@Service
@ConditionalOnMissingBean(TelegramBotNotificationService::class)
class NoOpNotificationService : NotificationService {

    private val logger = KotlinLogging.logger {}

    override fun sendNotification(userId: Long, message: String) {
        logger.debug { "Telegram bot is disabled. Notification skipped for user $userId" }
    }

    override fun sendNotificationToAll(message: String) {
        logger.debug { "Telegram bot is disabled. Broadcast notification skipped" }
    }
}
