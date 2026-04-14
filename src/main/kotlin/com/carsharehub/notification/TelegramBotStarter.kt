package com.carsharehub.notification

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Component
@ConditionalOnProperty(name = ["telegram.bot.enabled"], havingValue = "true")
class TelegramBotStarter(
    private val telegramBot: TelegramLongPollingBot
) {

    private val logger = KotlinLogging.logger {}

    init {
        try {
            val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
            botsApi.registerBot(telegramBot)
            logger.info { "Telegram bot registered successfully" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to register Telegram bot" }
        }
    }
}
