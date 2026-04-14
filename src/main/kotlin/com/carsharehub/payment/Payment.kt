package com.carsharehub.payment

import com.carsharehub.rental.Rental
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "payments")
class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus = PaymentStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: PaymentType = PaymentType.PAYMENT,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id")
    var rental: Rental? = null,

    @Column(name = "session_url", nullable = false)
    var sessionUrl: String = "",

    @Column(name = "session_id", nullable = false)
    var sessionId: String = "",

    @Column(name = "amount_to_pay", nullable = false)
    var amountToPay: BigDecimal = BigDecimal.ZERO
)
