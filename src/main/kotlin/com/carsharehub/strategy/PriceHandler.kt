package com.carsharehub.strategy

import com.carsharehub.rental.RentalDto
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

interface PriceHandler {
    fun getTotalPrice(
        rentalDto: RentalDto,
        carDailyFee: BigDecimal,
        fineMultiplier: BigDecimal,
        smallChangeIsInBanknote: BigDecimal
    ): Int

    fun calculationPriceByPeriodAndIsFine(
        period: BigDecimal,
        dailyFee: BigDecimal,
        isFine: Boolean,
        fineMultiplier: BigDecimal,
        smallChangeIsInBanknote: BigDecimal
    ): Int {
        var price = period.multiply(dailyFee).multiply(smallChangeIsInBanknote)
        if (isFine) {
            price = price.multiply(fineMultiplier)
        }
        return price.toInt()
    }

    fun daysInRent(start: LocalDate, end: LocalDate, addDay: Boolean): BigDecimal {
        var endDate = end
        if (addDay) {
            endDate = endDate.plusDays(1)
        }
        return BigDecimal(ChronoUnit.DAYS.between(start, endDate))
    }
}
