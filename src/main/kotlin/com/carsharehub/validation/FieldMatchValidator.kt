package com.carsharehub.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.beans.BeanWrapperImpl

class FieldMatchValidator : ConstraintValidator<FieldMatch, Any> {

    private lateinit var firstFieldName: String
    private lateinit var secondFieldName: String
    private lateinit var message: String

    override fun initialize(constraintAnnotation: FieldMatch) {
        firstFieldName = constraintAnnotation.first
        secondFieldName = constraintAnnotation.second
        message = constraintAnnotation.message
    }

    override fun isValid(value: Any, context: ConstraintValidatorContext): Boolean {
        val beanWrapper = BeanWrapperImpl(value)
        val firstObj = beanWrapper.getPropertyValue(firstFieldName)
        val secondObj = beanWrapper.getPropertyValue(secondFieldName)

        val isValid = firstObj == secondObj

        if (!isValid) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(secondFieldName)
                .addConstraintViolation()
        }

        return isValid
    }
}
