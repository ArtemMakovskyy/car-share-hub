package com.carsharehub.rental

import com.carsharehub.validation.ValidDateOrderConstraint
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@ValidDateOrderConstraint(
    field = "rentalDate",
    fieldMatch = "returnDate",
    message = "Rental date should be earlier than return date"
)
class CreateRentalRequestDto {

    @NotNull(message = "Rental date should not be null")
    @Schema(example = "2023-12-06")
    var rentalDate: LocalDate? = null

    @NotNull(message = "Return date should not be null")
    @Schema(example = "2023-12-16")
    var returnDate: LocalDate? = null

    @NotNull(message = "Car ID should not be null")
    @Schema(example = "2")
    var carId: Long? = null
}
