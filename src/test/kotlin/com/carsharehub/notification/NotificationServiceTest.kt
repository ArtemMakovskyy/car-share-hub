package com.carsharehub.notification

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class TelegramBotNotificationServiceTest {

    private val telegramBot = mock<org.telegram.telegrambots.bots.TelegramLongPollingBot>()
    private val credentialProvider = mock<TelegramBotCredentialProvider>()
    private lateinit var notificationService: TelegramBotNotificationService

    @BeforeEach
    fun setUp() {
        notificationService = TelegramBotNotificationService(telegramBot, credentialProvider)
    }

    @Test
    fun `sendNotification should send message when chat ID exists`() {
        val userId = 1L
        val chatId = "test_chat_123"
        val message = "Test notification"

        whenever(credentialProvider.getChatIdByUserId(userId)).thenReturn(chatId)
        whenever(telegramBot.execute(any<SendMessage>())).thenReturn(mock())

        notificationService.sendNotification(userId, message)

        verify(credentialProvider).getChatIdByUserId(userId)
        verify(telegramBot).execute(argThat<SendMessage> {
            this.chatId == chatId && this.text == message
        })
    }

    @Test
    fun `sendNotification should not send message when chat ID does not exist`() {
        val userId = 1L
        val message = "Test notification"

        whenever(credentialProvider.getChatIdByUserId(userId)).thenReturn(null)

        notificationService.sendNotification(userId, message)

        verify(credentialProvider).getChatIdByUserId(userId)
        verifyNoInteractions(telegramBot)
    }

    @Test
    fun `sendNotificationToAll should send messages to all registered chats`() {
        val message = "Broadcast notification"
        val chatIds = listOf("chat_1", "chat_2", "chat_3")

        whenever(credentialProvider.getAllChatIds()).thenReturn(chatIds)
        whenever(telegramBot.execute(any<SendMessage>())).thenReturn(mock())

        notificationService.sendNotificationToAll(message)

        verify(credentialProvider).getAllChatIds()
        verify(telegramBot, times(3)).execute(any<SendMessage>())
    }

    @Test
    fun `sendNotificationToAll should handle empty chat list`() {
        val message = "Broadcast notification"

        whenever(credentialProvider.getAllChatIds()).thenReturn(emptyList())

        notificationService.sendNotificationToAll(message)

        verify(credentialProvider).getAllChatIds()
        verifyNoInteractions(telegramBot)
    }

    @Test
    fun `sendNotification should handle TelegramApiException gracefully`() {
        val userId = 1L
        val chatId = "test_chat_123"
        val message = "Test notification"

        whenever(credentialProvider.getChatIdByUserId(userId)).thenReturn(chatId)
        whenever(telegramBot.execute(any<SendMessage>())).thenThrow(TelegramApiException("API Error"))

        assertDoesNotThrow {
            notificationService.sendNotification(userId, message)
        }
    }
}
