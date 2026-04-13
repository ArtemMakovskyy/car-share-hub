package com.carsharehub.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User management", description = "Endpoints for managing user profile")
@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "Bearer Authentication")
class UserController(
    private val userService: UserService
) {

    @Operation(summary = "Get current user profile", description = "Retrieve profile of the authenticated user")
    @GetMapping("/me")
    fun getProfile(authentication: Authentication): UserResponseDto {
        return userService.getUserFromAuthentication(authentication)
    }

    @Operation(summary = "Update user profile", description = "Update email, name, and password")
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun updateProfile(
        authentication: Authentication,
        @RequestBody @Valid requestDto: UserRegistrationRequestDto
    ): UserResponseDto {
        return userService.updateInfo(authentication, requestDto)
    }
}
