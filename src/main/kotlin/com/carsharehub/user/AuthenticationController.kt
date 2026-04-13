package com.carsharehub.user

import com.carsharehub.exception.RegistrationException
import com.carsharehub.security.AuthenticationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Authentication management", description = "Endpoints for login and registration")
@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val userService: UserService,
    private val authenticationService: AuthenticationService
) {

    @Operation(summary = "Register a new user", description = "Provide email, password, first name, and last name")
    @PostMapping("/register")
    @Throws(RegistrationException::class)
    fun registerUser(@RequestBody @Valid requestDto: UserRegistrationRequestDto): UserResponseDto {
        return userService.register(requestDto)
    }

    @Operation(summary = "User login", description = "Provide email and password to get JWT token")
    @PostMapping("/login")
    fun loginUser(@RequestBody @Valid requestDto: UserLoginRequestDto): UserLoginResponseDto {
        return authenticationService.authenticate(requestDto)
    }
}
