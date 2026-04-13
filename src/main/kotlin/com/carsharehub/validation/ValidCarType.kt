package com.carsharehub.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [CarTypeValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidCarType(
    val message: String = "Invalid car type. Must be one of: SEDAN, SUV, HATCHBACK, UNIVERSAL, COUPE, MINIVAN, PICKUP, CABRIOLET",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
