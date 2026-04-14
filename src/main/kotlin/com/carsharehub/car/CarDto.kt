package com.carsharehub.car

import java.math.BigDecimal

data class CarDto(
    var id: Long = 0,
    var model: String = "",
    var brand: String = "",
    var type: CarType = CarType.SEDAN,
    var inventoryQuantity: Int = 0,
    var dailyFee: BigDecimal = BigDecimal.ZERO
)
