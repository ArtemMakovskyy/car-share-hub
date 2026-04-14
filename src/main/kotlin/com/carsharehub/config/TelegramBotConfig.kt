package com.carsharehub.config

import com.carsharehub.notification.TelegramBotCredentialProvider
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.bots.TelegramLongPollingBot

@Configuration
@ConditionalOnProperty(name = ["telegram.bot.enabled"], havingValue = "true")
class TelegramBotConfig(
    private val credentialProvider: TelegramBotCredentialProvider
) {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun telegramBot(): TelegramLongPollingBot {
        return object : TelegramLongPollingBot() {
            override fun getBotUsername(): String = credentialProvider.botUsername

            override fun getBotToken(): String = credentialProvider.botToken

            override fun onUpdateReceived(update: org.telegram.telegrambots.meta.api.objects.Update) {
                if (update.hasMessage() && update.message.hasText()) {
                    val chatId = update.message.chatId.toString()
                    val userId = update.message.from.id
                    val text = update.message.text

                    logger.info { "Received message from user $userId (chat: $chatId): $text" }

                    if (text == "/start") {
                        credentialProvider.registerUserChat(userId, chatId)
                        logger.info { "User $userId registered with chat ID $chatId" }
                    }
                }
            }
        }
    }
}
