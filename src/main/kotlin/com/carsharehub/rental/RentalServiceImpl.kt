package com.carsharehub.rental

import com.carsharehub.car.CarRepository
import com.carsharehub.exception.DataDuplicationException
import com.carsharehub.exception.EntityNotFoundException
import com.carsharehub.user.User
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class RentalServiceImpl(
    private val rentalRepository: RentalRepository,
    private val carRepository: CarRepository,
    private val rentalMapper: RentalMapper
) : RentalService {

    @Transactional
    override fun add(requestDto: CreateRentalRequestDto, user: User): RentalDto {
        val car = carRepository.findById(requestDto.carId!!)
            .orElseThrow { EntityNotFoundException("Can't find car by id ${requestDto.carId}") }

        if (car.inventoryQuantity == 0) {
            throw EntityNotFoundException("These car models are out of stock for rent")
        }

        car.inventoryQuantity -= 1
        checkDuplicateActiveRental(user.id!!)

        val rental = rentalMapper.toEntity(requestDto)
        rental.car = car
        rental.user = user
        rental.isActive = true

        carRepository.save(car)

        return rentalMapper.toDto(rentalRepository.save(rental))
    }

    override fun findAllByUserIdAndStatus(
        userId: Long?,
        isActive: Boolean,
        pageable: Pageable
    ): List<RentalDto> {
        val page = if (userId == null) {
            rentalRepository.findByIsActive(isActive, pageable)
        } else {
            rentalRepository.findAllByUserIdAndActive(userId, isActive, pageable)
        }
        return page.content.map { rentalMapper.toDto(it) }
    }

    override fun getUserRentalDetailsByAuthentication(userId: Long): RentalDto {
        return rentalRepository.findAllByUserIdAndActive(userId, true, Pageable.unpaged())
            .content
            .map { rentalMapper.toDto(it) }
            .firstOrNull()
            ?: throw EntityNotFoundException("Can't find rental by user id $userId")
    }

    @Transactional
    override fun returnRentalCar(userId: Long): RentalDto {
        val rental = rentalRepository.findAllByUserIdAndActive(userId, true, Pageable.unpaged())
            .content
            .firstOrNull()
            ?: throw EntityNotFoundException("You can't return a car that you did not rent")

        rental.actualReturnDate = LocalDate.now()
        rental.isActive = false
        rentalRepository.save(rental)

        val car = carRepository.findById(rental.car.id!!)
            .orElseThrow { EntityNotFoundException("Can't get car by id ${rental.car.id}") }
        car.inventoryQuantity += 1
        carRepository.save(car)

        return rentalMapper.toDto(rental)
    }

    private fun checkDuplicateActiveRental(userId: Long) {
        val activeRentals = rentalRepository.findAllByUserIdAndActive(userId, true, Pageable.unpaged())
        if (activeRentals.content.isNotEmpty()) {
            throw DataDuplicationException("You can't rent two cars at the same time")
        }
    }
}
