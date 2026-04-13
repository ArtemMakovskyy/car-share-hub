package com.carsharehub.car

import com.carsharehub.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class CarServiceImpl(
    private val carRepository: CarRepository,
    private val carMapper: CarMapper
) : CarService {

    override fun findAll(): List<CarDto> =
        carRepository.findAll()
            .map { carMapper.toDto(it) }

    override fun findById(id: Long): CarDto =
        carRepository.findById(id)
            .map { carMapper.toDto(it) }
            .orElseThrow { EntityNotFoundException("Car with id $id not found") }

    override fun create(dto: CreateCarRequestDto): CarDto {
        val car = carMapper.toEntity(dto)
        val savedCar = carRepository.save(car)
        return carMapper.toDto(savedCar)
    }

    override fun update(id: Long, dto: CreateCarRequestDto): CarDto {
        val existingCar = carRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Car with id $id not found") }
        carMapper.updateCarFromDto(dto, existingCar)
        val savedCar = carRepository.save(existingCar)
        return carMapper.toDto(savedCar)
    }

    override fun delete(id: Long) {
        if (!carRepository.existsById(id)) {
            throw EntityNotFoundException("Car with id $id not found")
        }
        carRepository.deleteById(id)
    }
}
