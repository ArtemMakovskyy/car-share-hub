package com.carsharehub.user

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = [
    "telegram.bot.enabled=false",
    "telegram.bot.username=test_bot",
    "telegram.bot.token=test_token"
])
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        roleRepository.deleteAll()
        roleRepository.save(Role(name = RoleName.ROLE_CUSTOMER))
        roleRepository.save(Role(name = RoleName.ROLE_ADMIN))
        roleRepository.save(Role(name = RoleName.ROLE_MANAGER))
    }

    @Test
    fun `register user should return 200`() {
        val requestDto = UserRegistrationRequestDto().apply {
            email = "test@example.com"
            firstName = "John"
            lastName = "Doe"
            password = "password1234"
            repeatPassword = "password1234"
        }

        val response = mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isOk)
            .andReturn()
            .response

        val responseDto = objectMapper.readValue(response.contentAsString, UserResponseDto::class.java)
        assertEquals("test@example.com", responseDto.email)
        assertEquals("John", responseDto.firstName)
        assertEquals("Doe", responseDto.lastName)
        assertNotNull(responseDto.id)
    }

    @Test
    fun `register user with duplicate email should return 400`() {
        val requestDto = UserRegistrationRequestDto().apply {
            email = "duplicate@example.com"
            firstName = "John"
            lastName = "Doe"
            password = "password1234"
            repeatPassword = "password1234"
        }

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        ).andExpect(status().isOk)

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `register user with invalid email should return 400`() {
        val requestDto = UserRegistrationRequestDto().apply {
            email = "invalid-email"
            firstName = "John"
            lastName = "Doe"
            password = "password1234"
            repeatPassword = "password1234"
        }

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `register user with mismatched passwords should return 400`() {
        val requestDto = UserRegistrationRequestDto().apply {
            email = "test@example.com"
            firstName = "John"
            lastName = "Doe"
            password = "password1234"
            repeatPassword = "differentPassword"
        }

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `login user should return 200 with token`() {
        val registerDto = UserRegistrationRequestDto().apply {
            email = "login@example.com"
            firstName = "John"
            lastName = "Doe"
            password = "password1234"
            repeatPassword = "password1234"
        }

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto))
        ).andExpect(status().isOk)

        val loginDto = UserLoginRequestDto().apply {
            email = "login@example.com"
            password = "password1234"
        }

        val response = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )
            .andExpect(status().isOk)
            .andReturn()
            .response

        val loginResponse = objectMapper.readValue(response.contentAsString, UserLoginResponseDto::class.java)
        assertNotNull(loginResponse.token)
        assertTrue(loginResponse.token.isNotEmpty())
    }

    @Test
    fun `login with wrong password should return 401`() {
        val registerDto = UserRegistrationRequestDto().apply {
            email = "wrongpass@example.com"
            firstName = "John"
            lastName = "Doe"
            password = "password1234"
            repeatPassword = "password1234"
        }

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto))
        ).andExpect(status().isOk)

        val loginDto = UserLoginRequestDto().apply {
            email = "wrongpass@example.com"
            password = "wrongpassword"
        }

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        )
            .andExpect(status().isUnauthorized)
    }
}
