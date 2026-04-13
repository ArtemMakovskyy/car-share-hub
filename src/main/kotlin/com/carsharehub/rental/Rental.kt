package com.carsharehub.rental

import com.carsharehub.car.Car
import com.carsharehub.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.LocalDate

@Entity
@Table(name = "rentals")
@SQLDelete(sql = "UPDATE rentals SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
class Rental(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "rental_date", nullable = false)
    var rentalDate: LocalDate,

    @Column(name = "return_date", nullable = false)
    var returnDate: LocalDate,

    @Column(name = "actual_return_date")
    var actualReturnDate: LocalDate? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    var car: Car,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = false
)
