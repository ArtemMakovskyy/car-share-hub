package com.carsharehub.validation

import com.carsharehub.user.RoleName
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class UserRoleValidator : ConstraintValidator<ValidUserRole, String> {

    override fun isValid(role: String?, context: ConstraintValidatorContext): Boolean {
        if (role.isNullOrBlank()) return false
        return runCatching { RoleName.valueOf(role) }.isSuccess
    }
}
