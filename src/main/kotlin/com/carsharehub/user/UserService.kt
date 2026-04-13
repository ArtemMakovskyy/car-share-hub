package com.carsharehub.user

import org.springframework.security.core.Authentication

interface UserService {

    fun register(request: UserRegistrationRequestDto): UserResponseDto

    fun getUserFromAuthentication(authentication: Authentication): UserResponseDto

    fun updateRole(userId: Long, role: String): UserResponseDto

    fun updateInfo(authentication: Authentication, requestDto: UserRegistrationRequestDto): UserResponseDto
}
