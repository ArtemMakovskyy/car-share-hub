package com.carsharehub.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

class UserLoginRequestDto {

    @Email(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email format")
    @Schema(example = "customer@email.com")
    var email: String = ""

    @Size(min = 4, max = 20, message = "Password must be from 4 to 20 characters")
    var password: String = ""
}
