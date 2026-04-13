package com.carsharehub.car

data class CarDto(
    var id: Long = 0,
    var model: String = "",
    var brand: String = "",
    var type: CarType = CarType.SEDAN,
    var inventoryQuantity: Int = 0,
    var dailyFee: Double = 0.0
)
