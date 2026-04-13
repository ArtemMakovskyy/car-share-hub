package com.carsharehub.payment

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

class PaymentDto {

    var id: Long? = null

    @Schema(example = "PENDING")
    var status: String = ""

    @Schema(example = "PAYMENT")
    var type: String = ""

    var rentalId: Long? = null

    var sessionId: String = ""

    var amountToPay: BigDecimal? = null

    var sessionUrl: String = ""
}
