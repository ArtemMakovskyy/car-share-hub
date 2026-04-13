package com.carsharehub.car

import com.carsharehub.validation.ValidCarType
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateCarRequestDto(
    @field:NotBlank
    val model: String = "",

    @field:NotBlank
    val brand: String = "",

    @field:NotBlank
    @field:ValidCarType
    val type: String = "",

    @field:Min(0)
    @field:Max(Long.MAX_VALUE)
    val inventoryQuantity: Int = 0,

    @field:Positive
    val dailyFee: Double = 0.0
)
