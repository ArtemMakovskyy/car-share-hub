package com.carsharehub.payment

import org.springframework.data.domain.Pageable

interface PaymentService {

    fun findAllByRentalUserId(userId: Long, pageable: Pageable): List<PaymentDto>

    fun createPaymentSession(createPaymentSessionDto: CreatePaymentSessionDto): PaymentDto

    fun handleSuccessfulPayment(sessionId: String): String

    fun processPaymentCancellation(sessionId: String): String
}
