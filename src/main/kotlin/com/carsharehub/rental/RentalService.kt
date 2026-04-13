package com.carsharehub.rental

import com.carsharehub.user.User
import org.springframework.data.domain.Pageable

interface RentalService {

    fun add(requestDto: CreateRentalRequestDto, user: User): RentalDto

    fun findAllByUserIdAndStatus(userId: Long?, isActive: Boolean, pageable: Pageable): List<RentalDto>

    fun getUserRentalDetailsByAuthentication(userId: Long): RentalDto

    fun returnRentalCar(userId: Long): RentalDto
}
