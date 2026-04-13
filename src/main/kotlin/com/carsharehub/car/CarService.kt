package com.carsharehub.car

interface CarService {
    fun findAll(): List<CarDto>
    fun findById(id: Long): CarDto
    fun create(dto: CreateCarRequestDto): CarDto
    fun update(id: Long, dto: CreateCarRequestDto): CarDto
    fun delete(id: Long)
}
