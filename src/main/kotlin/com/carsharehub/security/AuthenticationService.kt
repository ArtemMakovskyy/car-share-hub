package com.carsharehub.security

import com.carsharehub.user.UserLoginRequestDto
import com.carsharehub.user.UserLoginResponseDto
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {

    fun authenticate(request: UserLoginRequestDto): UserLoginResponseDto {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        val token = jwtUtil.generateToken(authentication.name)
        return UserLoginResponseDto(token)
    }
}
