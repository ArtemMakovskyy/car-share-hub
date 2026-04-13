package com.carsharehub.user

import com.carsharehub.validation.FieldMatch
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@FieldMatch(
    first = "password",
    second = "repeatPassword",
    message = "Passwords do not match"
)
class UserRegistrationRequestDto {

    @Email(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email format")
    @Schema(example = "customer@email.com")
    var email: String = ""

    @Size(min = 1, message = "First name must not be blank")
    @Schema(example = "John")
    var firstName: String = ""

    @Size(min = 1, message = "Last name must not be blank")
    @Schema(example = "Doe")
    var lastName: String = ""

    @Size(min = 4, max = 20, message = "Password must be from 4 to 20 characters")
    @Schema(example = "password1234")
    var password: String = ""

    @Schema(example = "password1234")
    var repeatPassword: String = ""
}
