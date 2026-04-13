package com.carsharehub.rental

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

class RentalDto {

    var id: Long? = null

    @Schema(example = "2023-12-10")
    var rentalDate: LocalDate? = null

    @Schema(example = "2023-12-16")
    var returnDate: LocalDate? = null

    @Schema(example = "2023-12-20")
    var actualReturnDate: LocalDate? = null

    var carId: Long? = null

    var userId: Long? = null
}
