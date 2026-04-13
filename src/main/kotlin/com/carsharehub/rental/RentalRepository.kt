package com.carsharehub.rental

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RentalRepository : JpaRepository<Rental, Long> {

    @Query("FROM Rental r WHERE r.user.id = :userId AND r.isActive = :isActive")
    fun findAllByUserIdAndActive(userId: Long, isActive: Boolean, pageable: Pageable): Page<Rental>

    fun findByIsActive(isActive: Boolean, pageable: Pageable): Page<Rental>

    @Query("""
        FROM Rental r join FETCH r.user u join FETCH r.car c
        WHERE r.isDeleted = false
        AND r.isActive = true
        AND u.telegramChatId IS NOT NULL
    """)
    fun findAllDetailedActiveRentalsWithTelegramChatId(): List<Rental>
}
