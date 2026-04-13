package com.carsharehub.payment

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface PaymentRepository : JpaRepository<Payment, Long> {

    @Query("FROM Payment p WHERE p.rental.user.id = :userId")
    fun findAllByRentalUserId(pageable: Pageable, userId: Long): Page<Payment>

    fun findBySessionId(sessionId: String): Optional<Payment>
}
