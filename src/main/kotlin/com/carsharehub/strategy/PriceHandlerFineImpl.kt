package com.carsharehub.strategy

import com.carsharehub.rental.RentalDto
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PriceHandlerFineImpl : PriceHandler {

    override fun getTotalPrice(
        rentalDto: RentalDto,
        carDailyFee: BigDecimal,
        fineMultiplier: BigDecimal,
        smallChangeIsInBanknote: BigDecimal
    ): Int {
        val actualReturnDate = rentalDto.actualReturnDate ?: rentalDto.returnDate!!
        val daysInRent = daysInRent(rentalDto.rentalDate!!, actualReturnDate, true)
        return calculationPriceByPeriodAndIsFine(
            daysInRent, carDailyFee, true, fineMultiplier, smallChangeIsInBanknote
        )
    }
}
