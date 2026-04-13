package com.carsharehub.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [UserRoleValidator::class])
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidUserRole(
    val message: String = "Invalid user role",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
