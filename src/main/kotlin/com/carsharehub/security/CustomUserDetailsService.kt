package com.carsharehub.security

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService : UserDetailsService {

    override fun loadUserByUsername(username: String): org.springframework.security.core.userdetails.UserDetails {
        // TODO: implement user lookup from database
        throw UsernameNotFoundException("User not found: $username")
    }
}
