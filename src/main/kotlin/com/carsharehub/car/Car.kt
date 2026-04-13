package com.carsharehub.car

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "cars")
data class Car(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var model: String = "",

    @Column(nullable = false, unique = true)
    var brand: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: CarType = CarType.SEDAN,

    @Column(nullable = false)
    var inventoryQuantity: Int = 0,

    @Column(nullable = false)
    var dailyFee: Double = 0.0
)
