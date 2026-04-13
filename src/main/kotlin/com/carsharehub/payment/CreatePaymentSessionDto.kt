package com.carsharehub.payment

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

class CreatePaymentSessionDto {

    @NotNull
    @Positive
    var rentalId: Long? = null

    @NotBlank
    var paymentType: String = ""
}
