package com.carsharehub.user

import com.carsharehub.security.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var authToken: String

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        roleRepository.deleteAll()
        val roleCustomer = roleRepository.save(Role(name = RoleName.ROLE_CUSTOMER))

        val user = User().apply {
            email = "user@example.com"
            firstName = "Test"
            lastName = "User"
            setPassword(passwordEncoder.encode("password1234"))
            roles = mutableSetOf(roleCustomer)
        }
        userRepository.save(user)

        authToken = jwtUtil.generateToken("user@example.com")
    }

    @Test
    fun `get current user profile should return 200`() {
        mockMvc.perform(
            get("/users/me")
                .header("Authorization", "Bearer $authToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"))
            .andExpect(jsonPath("$.roles").isArray)
    }

    @Test
    fun `get profile without auth should return 401`() {
        mockMvc.perform(get("/users/me"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `update user profile should return 200`() {
        val updateDto = UserRegistrationRequestDto().apply {
            email = "updated@example.com"
            firstName = "Updated"
            lastName = "Name"
            password = "newpassword123"
            repeatPassword = "newpassword123"
        }

        val response = mockMvc.perform(
            put("/users/me")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isOk)
            .andReturn()
            .response

        val responseDto = objectMapper.readValue(response.contentAsString, UserResponseDto::class.java)
        assertEquals("updated@example.com", responseDto.email)
        assertEquals("Updated", responseDto.firstName)
        assertEquals("Name", responseDto.lastName)
        assertNotNull(responseDto.id)
    }

    @Test
    fun `update profile with invalid email should return 400`() {
        val updateDto = UserRegistrationRequestDto().apply {
            email = "invalid"
            firstName = "Test"
            lastName = "User"
            password = "password1234"
            repeatPassword = "password1234"
        }

        mockMvc.perform(
            put("/users/me")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `update profile with mismatched passwords should return 400`() {
        val updateDto = UserRegistrationRequestDto().apply {
            email = "user@example.com"
            firstName = "Test"
            lastName = "User"
            password = "password1234"
            repeatPassword = "different"
        }

        mockMvc.perform(
            put("/users/me")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `update profile with wrong auth token should return 401`() {
        val updateDto = UserRegistrationRequestDto().apply {
            email = "user@example.com"
            firstName = "Test"
            lastName = "User"
            password = "password1234"
            repeatPassword = "password1234"
        }

        mockMvc.perform(
            put("/users/me")
                .header("Authorization", "Bearer invalidtoken123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.error").exists())
    }
}
