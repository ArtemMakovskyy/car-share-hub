package com.carsharehub.rental

import com.carsharehub.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Rental management", description = "Endpoints for managing rentals")
@RestController
@RequestMapping("/rentals")
@SecurityRequirement(name = "Bearer Authentication")
class RentalController(
    private val rentalService: RentalService
) {

    @Operation(summary = "Add new rental", description = "Add new car rental and decrease car inventory by 1")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    fun addRental(
        @RequestBody @Valid requestDto: CreateRentalRequestDto,
        authentication: Authentication
    ): RentalDto {
        val user = authentication.principal as User
        return rentalService.add(requestDto, user)
    }

    @Operation(
        summary = "Get the rentals by user id and status",
        description = """
            Retrieve rentals by user id and active status.
            If user is a CUSTOMER he can get only own data,
            if an ADMIN OR MANAGER can get any data.
        """
    )
    @GetMapping("/")
    fun getRentalsByUserIdAndRentalStatus(
        @RequestParam(name = "user_id", required = false) userId: Long?,
        @RequestParam(name = "is_active", defaultValue = "true") isActive: Boolean,
        pageable: org.springframework.data.domain.Pageable,
        authentication: Authentication
    ): List<RentalDto> {
        val user = authentication.principal as User
        val effectiveUserId = if (user.authorities.contains(SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            user.id
        } else {
            userId
        }
        return rentalService.findAllByUserIdAndStatus(effectiveUserId, isActive, pageable)
    }

    @Operation(summary = "Get information about rental", description = "CUSTOMER can get information about his own rental")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    fun getUserRentalDetails(authentication: Authentication): RentalDto {
        val user = authentication.principal as User
        return rentalService.getUserRentalDetailsByAuthentication(user.id!!)
    }

    @Operation(summary = "Return rental car by CUSTOMER", description = "Return rental car by CUSTOMER and increase car inventory by 1")
    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    fun returnRental(authentication: Authentication): RentalDto {
        val user = authentication.principal as User
        return rentalService.returnRentalCar(user.id!!)
    }
}
