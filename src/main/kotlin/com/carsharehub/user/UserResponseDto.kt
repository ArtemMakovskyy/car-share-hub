package com.carsharehub.user

import io.swagger.v3.oas.annotations.media.Schema

class UserResponseDto {

    var id: Long? = null

    @Schema(example = "customer@email.com")
    var email: String = ""

    @Schema(example = "John")
    var firstName: String = ""

    @Schema(example = "Doe")
    var lastName: String = ""

    @Schema(example = "ROLE_CUSTOMER")
    var roles: Set<String> = emptySet()
}
