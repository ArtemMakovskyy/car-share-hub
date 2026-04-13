package com.carsharehub.strategy

import com.carsharehub.rental.RentalDto
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Component
class PriceHandlerPaymentImpl : PriceHandler {

    override fun getTotalPrice(
        rentalDto: RentalDto,
        carDailyFee: BigDecimal,
        fineMultiplier: BigDecimal,
        smallChangeIsInBanknote: BigDecimal
    ): Int {
        val returnDate = rentalDto.returnDate!!
        val actualReturnDate = rentalDto.actualReturnDate

        if (actualReturnDate == null || actualReturnDate.isEqual(returnDate) || actualReturnDate.isBefore(returnDate)) {
            val actualDate = actualReturnDate ?: returnDate
            val daysInRent = daysInRent(rentalDto.rentalDate!!, actualDate, true)
            return calculationPriceByPeriodAndIsFine(
                daysInRent, carDailyFee, false, fineMultiplier, smallChangeIsInBanknote
            )
        }

        val daysInRentWithoutFine = daysInRent(rentalDto.rentalDate!!, returnDate, true)
        val daysInRentWithFine = daysInRent(returnDate, actualReturnDate, false)

        return calculationPriceByPeriodAndIsFine(
            daysInRentWithoutFine, carDailyFee, false, fineMultiplier, smallChangeIsInBanknote
        ) + calculationPriceByPeriodAndIsFine(
            daysInRentWithFine, carDailyFee, true, fineMultiplier, smallChangeIsInBanknote
        )
    }
}
