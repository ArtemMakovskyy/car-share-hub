package com.carsharehub.car

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "cars")
data class Car(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "model", nullable = false)
    var model: String = "",

    @Column(name = "brand", nullable = false)
    var brand: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: CarType = CarType.SEDAN,

    @Column(name = "inventory", nullable = false)
    var inventoryQuantity: Int = 0,

    @Column(name = "daily_fee", nullable = false, precision = 10, scale = 2)
    var dailyFee: BigDecimal = BigDecimal.ZERO,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false
)
