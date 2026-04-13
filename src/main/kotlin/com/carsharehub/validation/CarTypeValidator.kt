package com.carsharehub.validation

import com.carsharehub.car.CarType
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class CarTypeValidator : ConstraintValidator<ValidCarType, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) {
            return false
        }
        return CarType.entries.any { it.name == value.uppercase() }
    }
}
