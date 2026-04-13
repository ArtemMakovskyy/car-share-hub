package com.carsharehub.rental

import com.carsharehub.car.Car
import com.carsharehub.car.CarRepository
import com.carsharehub.car.CarType
import com.carsharehub.user.*
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
class RentalControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var carRepository: CarRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jwtUtil: com.carsharehub.security.JwtUtil

    @Autowired
    private lateinit var rentalRepository: RentalRepository

    private lateinit var authToken: String
    private lateinit var adminAuthToken: String

    @BeforeEach
    fun setUp() {
        rentalRepository.deleteAll()
        carRepository.deleteAll()
        userRepository.deleteAll()
        roleRepository.deleteAll()

        val roleCustomer = roleRepository.save(Role(name = RoleName.ROLE_CUSTOMER))
        val roleAdmin = roleRepository.save(Role(name = RoleName.ROLE_ADMIN))

        val user = User().apply {
            email = "customer@example.com"
            firstName = "John"
            lastName = "Doe"
            setPassword(passwordEncoder.encode("password1234"))
            roles = mutableSetOf(roleCustomer)
        }
        userRepository.save(user)

        val admin = User().apply {
            email = "admin@example.com"
            firstName = "Admin"
            lastName = "User"
            setPassword(passwordEncoder.encode("admin1234"))
            roles = mutableSetOf(roleAdmin)
        }
        userRepository.save(admin)

        authToken = jwtUtil.generateToken("customer@example.com")
        adminAuthToken = jwtUtil.generateToken("admin@example.com")
    }

    @Test
    fun `create rental should return 201`() {
        val car = carRepository.save(
            Car(model = "Civic-Test1", brand = "Honda", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )

        val requestDto = CreateRentalRequestDto().apply {
            rentalDate = java.time.LocalDate.now().plusDays(1)
            returnDate = java.time.LocalDate.now().plusDays(7)
            carId = car.id
        }

        val response = mockMvc.perform(
            post("/rentals")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isCreated)
            .andReturn()
            .response

        val responseDto = objectMapper.readValue(response.contentAsString, RentalDto::class.java)
        assertNotNull(responseDto.id)
        assertEquals(car.id, responseDto.carId)
    }

    @Test
    fun `create rental without auth should return 401`() {
        val requestDto = CreateRentalRequestDto().apply {
            rentalDate = java.time.LocalDate.now().plusDays(1)
            returnDate = java.time.LocalDate.now().plusDays(7)
            carId = 1L
        }

        mockMvc.perform(
            post("/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `create rental for non-existing car should return 404`() {
        val requestDto = CreateRentalRequestDto().apply {
            rentalDate = java.time.LocalDate.now().plusDays(1)
            returnDate = java.time.LocalDate.now().plusDays(7)
            carId = 999L
        }

        mockMvc.perform(
            post("/rentals")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `create rental for out of stock car should return 404`() {
        val car = carRepository.save(
            Car(model = "Accord-OOS", brand = "Honda", type = CarType.SEDAN,
                inventoryQuantity = 0, dailyFee = 60.0)
        )

        val requestDto = CreateRentalRequestDto().apply {
            rentalDate = java.time.LocalDate.now().plusDays(1)
            returnDate = java.time.LocalDate.now().plusDays(7)
            carId = car.id
        }

        mockMvc.perform(
            post("/rentals")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `create second rental while one is active should return 409`() {
        val car = carRepository.save(
            Car(model = "Civic-Dup", brand = "Honda-Dup", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )
        val car2 = carRepository.save(
            Car(model = "Accord-Dup", brand = "Toyota-Dup", type = CarType.SEDAN,
                inventoryQuantity = 3, dailyFee = 60.0)
        )

        val requestDto = CreateRentalRequestDto().apply {
            rentalDate = java.time.LocalDate.now().plusDays(1)
            returnDate = java.time.LocalDate.now().plusDays(7)
            carId = car.id
        }

        mockMvc.perform(
            post("/rentals")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        ).andExpect(status().isCreated)

        val requestDto2 = CreateRentalRequestDto().apply {
            rentalDate = java.time.LocalDate.now().plusDays(2)
            returnDate = java.time.LocalDate.now().plusDays(8)
            carId = car2.id
        }

        mockMvc.perform(
            post("/rentals")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto2))
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `get user rental details should return 200`() {
        val car = carRepository.save(
            Car(model = "Civic-Details", brand = "Honda", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )

        val requestDto = CreateRentalRequestDto().apply {
            rentalDate = java.time.LocalDate.now().plusDays(1)
            returnDate = java.time.LocalDate.now().plusDays(7)
            carId = car.id
        }

        mockMvc.perform(
            post("/rentals")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        ).andExpect(status().isCreated)

        mockMvc.perform(
            get("/rentals")
                .header("Authorization", "Bearer $authToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.carId").value(car.id))
    }

    @Test
    fun `return rental should return 200`() {
        val car = carRepository.save(
            Car(model = "Civic-Return", brand = "Honda", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )

        val requestDto = CreateRentalRequestDto().apply {
            rentalDate = java.time.LocalDate.now().plusDays(1)
            returnDate = java.time.LocalDate.now().plusDays(7)
            carId = car.id
        }

        mockMvc.perform(
            post("/rentals")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        ).andExpect(status().isCreated)

        val response = mockMvc.perform(
            post("/rentals/return")
                .header("Authorization", "Bearer $authToken")
        )
            .andExpect(status().isOk)
            .andReturn()
            .response

        val responseDto = objectMapper.readValue(response.contentAsString, RentalDto::class.java)
        assertNotNull(responseDto.actualReturnDate)
    }

    @Test
    fun `return rental without active rental should return 404`() {
        mockMvc.perform(
            post("/rentals/return")
                .header("Authorization", "Bearer $authToken")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get rentals by status as admin should return 200`() {
        mockMvc.perform(
            get("/rentals/")
                .header("Authorization", "Bearer $adminAuthToken")
                .param("is_active", "true")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `create rental with invalid dates should return 400`() {
        val car = carRepository.save(
            Car(model = "Civic-Invalid", brand = "Honda", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )

        val requestDto = CreateRentalRequestDto().apply {
            rentalDate = java.time.LocalDate.now().plusDays(10)
            returnDate = java.time.LocalDate.now().plusDays(5)
            carId = car.id
        }

        mockMvc.perform(
            post("/rentals")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create rental with null dates should return 400`() {
        val requestDto = CreateRentalRequestDto().apply {
            rentalDate = null
            returnDate = null
            carId = 1L
        }

        mockMvc.perform(
            post("/rentals")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isBadRequest)
    }
}
