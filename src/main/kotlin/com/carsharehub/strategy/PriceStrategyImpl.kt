package com.carsharehub.strategy

import com.carsharehub.payment.PaymentType
import com.carsharehub.strategy.PriceHandlerFineImpl
import com.carsharehub.strategy.PriceHandlerPaymentImpl
import org.springframework.stereotype.Component

@Component
class PriceStrategyImpl : PriceStrategy {

    private val paymentStrategy: Map<PaymentType, PriceHandler> = mapOf(
        PaymentType.PAYMENT to PriceHandlerPaymentImpl(),
        PaymentType.FINE to PriceHandlerFineImpl()
    )

    override fun get(paymentType: PaymentType): PriceHandler {
        return paymentStrategy[paymentType]
            ?: throw IllegalArgumentException("Unknown payment type: $paymentType")
    }
}
