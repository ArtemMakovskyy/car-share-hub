package com.carsharehub.security

import com.carsharehub.user.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): org.springframework.security.core.userdetails.UserDetails {
        return userRepository.findUserByEmail(username)
            .orElseThrow { UsernameNotFoundException("User not found: $username") }
    }
}
