package com.carsharehub.car

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/api/cars")
class CarController(
    private val carService: CarService
) {
    @GetMapping
    fun findAll(): ResponseEntity<List<CarDto>> =
        ResponseEntity.ok(carService.findAll())

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CarDto> =
        ResponseEntity.ok(carService.findById(id))

    @PostMapping
    fun create(@RequestBody @Valid dto: CreateCarRequestDto): ResponseEntity<CarDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(carService.create(dto))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid dto: CreateCarRequestDto
    ): ResponseEntity<CarDto> =
        ResponseEntity.ok(carService.update(id, dto))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        carService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
