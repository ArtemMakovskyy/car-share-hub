package com.carsharehub.strategy

import com.carsharehub.payment.PaymentType

interface PriceStrategy {
    fun get(paymentType: PaymentType): PriceHandler
}
