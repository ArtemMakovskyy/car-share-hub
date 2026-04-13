package com.carsharehub.user

import com.carsharehub.exception.EntityNotFoundException
import com.carsharehub.exception.RegistrationException
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: UserMapper
) : UserService {

    override fun register(request: UserRegistrationRequestDto): UserResponseDto {
        if (userRepository.findUserByEmail(request.email).isPresent) {
            throw RegistrationException("Unable to complete registration")
        }

        val user = User()
        user.email = request.email
        user.firstName = request.firstName
        user.lastName = request.lastName
        user.setPassword(passwordEncoder.encode(request.password))

        val roleCustomer = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
            .orElseThrow { RuntimeException("Can't find ROLE_CUSTOMER") }

        user.roles = mutableSetOf(roleCustomer)

        val savedUser = userRepository.save(user)
        return userMapper.toDto(savedUser)
    }

    override fun getUserFromAuthentication(authentication: Authentication): UserResponseDto {
        val principal = authentication.principal as User
        return userMapper.toDto(principal)
    }

    override fun updateRole(userId: Long, role: String): UserResponseDto {
        val userFromDb = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("Can't find user by id $userId") }

        val roleFromDb = roleRepository.findByName(RoleName.valueOf(role))
            .orElseThrow { EntityNotFoundException("Can't find role $role") }

        userFromDb.roles = mutableSetOf(roleFromDb)
        return userMapper.toDto(userRepository.save(userFromDb))
    }

    override fun updateInfo(
        authentication: Authentication,
        requestDto: UserRegistrationRequestDto
    ): UserResponseDto {
        val userFromAuthentication = authentication.principal as User
        userFromAuthentication.email = requestDto.email
        userFromAuthentication.firstName = requestDto.firstName
        userFromAuthentication.lastName = requestDto.lastName
        userFromAuthentication.setPassword(passwordEncoder.encode(requestDto.password))
        return userMapper.toDto(userRepository.save(userFromAuthentication))
    }
}
