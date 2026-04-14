package com.carsharehub.notification

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["telegram.bot.enabled"], havingValue = "true")
class TelegramBotCredentialProvider {

    @Value("\${telegram.bot.username}")
    lateinit var botUsername: String

    @Value("\${telegram.bot.token}")
    lateinit var botToken: String

    private val userChatIds = mutableMapOf<Long, String>()

    fun getChatIdByUserId(userId: Long): String? {
        return userChatIds[userId]
    }

    fun getAllChatIds(): List<String> {
        return userChatIds.values.toList()
    }

    fun registerUserChat(userId: Long, chatId: String) {
        userChatIds[userId] = chatId
    }

    fun removeUserChat(userId: Long) {
        userChatIds.remove(userId)
    }
}
