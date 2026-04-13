package com.carsharehub.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.LocalDate
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class DateOrderConstraintValidator :
    ConstraintValidator<ValidDateOrderConstraint, Any> {

    private lateinit var field: String
    private lateinit var fieldMatch: String

    override fun initialize(annotation: ValidDateOrderConstraint) {
        field = annotation.field
        fieldMatch = annotation.fieldMatch
    }

    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true

        val date1 = getFieldValue(value, field) as? LocalDate ?: return true
        val date2 = getFieldValue(value, fieldMatch) as? LocalDate ?: return true

        return date1.isBefore(date2)
    }

    private fun getFieldValue(obj: Any, fieldName: String): Any? {
        val kClass = obj::class
        val property = kClass.memberProperties.find { it.name == fieldName }
        return property?.getter?.call(obj)
    }
}
