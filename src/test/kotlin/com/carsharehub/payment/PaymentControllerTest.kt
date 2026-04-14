package com.carsharehub.payment

import com.carsharehub.car.Car
import com.carsharehub.car.CarRepository
import com.carsharehub.car.CarType
import com.carsharehub.rental.*
import com.carsharehub.user.*
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
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.context.TestPropertySource
import java.time.LocalDate

@TestPropertySource(properties = [
    "telegram.bot.enabled=false",
    "telegram.bot.username=test_bot",
    "telegram.bot.token=test_token"
])
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PaymentControllerTest {

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

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    private lateinit var authToken: String
    private lateinit var adminAuthToken: String

    @BeforeEach
    fun setUp() {
        paymentRepository.deleteAll()
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
    fun `create payment session should return 200`() {
        val car = carRepository.save(
            Car(model = "Civic-Pay", brand = "Honda-Pay", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )

        val rental = rentalRepository.save(
            Rental(
                rentalDate = LocalDate.now(),
                returnDate = LocalDate.now().plusDays(7),
                car = car,
                user = userRepository.findUserByEmail("customer@example.com").get(),
                isActive = true
            )
        )

        val requestDto = CreatePaymentSessionDto().apply {
            rentalId = rental.id
            paymentType = "PAYMENT"
        }

        val response = mockMvc.perform(
            post("/payments/")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isOk)
            .andReturn()
            .response

        val responseDto = objectMapper.readValue(response.contentAsString, PaymentDto::class.java)
        assertNotNull(responseDto.id)
        assertEquals("PENDING", responseDto.status)
        assertEquals("PAYMENT", responseDto.type)
        assertNotNull(responseDto.sessionUrl)
    }

    @Test
    fun `create payment session without auth should return 401`() {
        val requestDto = CreatePaymentSessionDto().apply {
            rentalId = 1L
            paymentType = "PAYMENT"
        }

        mockMvc.perform(
            post("/payments/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `create payment session for non-existing rental should return 404`() {
        val requestDto = CreatePaymentSessionDto().apply {
            rentalId = 999L
            paymentType = "PAYMENT"
        }

        mockMvc.perform(
            post("/payments/")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `handle successful payment should return 200`() {
        val car = carRepository.save(
            Car(model = "Civic-Success", brand = "Honda-Success", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )

        val rental = rentalRepository.save(
            Rental(
                rentalDate = LocalDate.now(),
                returnDate = LocalDate.now().plusDays(7),
                car = car,
                user = userRepository.findUserByEmail("customer@example.com").get(),
                isActive = true
            )
        )

        val payment = paymentRepository.save(
            Payment(
                sessionId = "test_session_success",
                status = PaymentStatus.PENDING,
                type = PaymentType.PAYMENT,
                rental = rental,
                amountToPay = java.math.BigDecimal("350.00"),
                sessionUrl = "https://checkout.stripe.com/test"
            )
        )

        val response = mockMvc.perform(
            get("/payments/success")
                .param("session_id", "test_session_success")
        )
            .andExpect(status().isOk)
            .andReturn()
            .response

        assertEquals("The rent payment was successful", response.contentAsString)

        val updatedPayment = paymentRepository.findById(payment.id!!).get()
        assertEquals(PaymentStatus.PAID, updatedPayment.status)
    }

    @Test
    fun `handle successful payment with invalid session should return 404`() {
        mockMvc.perform(
            get("/payments/success")
                .param("session_id", "invalid_session")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `process payment cancellation should return 200`() {
        val response = mockMvc.perform(
            get("/payments/cancel")
                .param("session_id", "test_session_cancel")
        )
            .andExpect(status().isOk)
            .andReturn()
            .response

        assertTrue(response.contentAsString.contains("payment can be made later"))
    }

    @Test
    fun `get payments as customer should return only own payments`() {
        val car = carRepository.save(
            Car(model = "Civic-List", brand = "Honda-List", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )

        val user = userRepository.findUserByEmail("customer@example.com").get()

        val rental = rentalRepository.save(
            Rental(
                rentalDate = LocalDate.now(),
                returnDate = LocalDate.now().plusDays(7),
                car = car,
                user = user,
                isActive = true
            )
        )

        paymentRepository.save(
            Payment(
                sessionId = "test_session_list",
                status = PaymentStatus.PENDING,
                type = PaymentType.PAYMENT,
                rental = rental,
                amountToPay = java.math.BigDecimal("350.00"),
                sessionUrl = "https://checkout.stripe.com/test"
            )
        )

        mockMvc.perform(
            get("/payments/")
                .header("Authorization", "Bearer $authToken")
                .param("user_id", user.id.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `get payments as admin should return all payments`() {
        mockMvc.perform(
            get("/payments/")
                .header("Authorization", "Bearer $adminAuthToken")
                .param("user_id", "1")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `create payment session with invalid payment type should return 400`() {
        val requestDto = CreatePaymentSessionDto().apply {
            rentalId = 1L
            paymentType = ""
        }

        mockMvc.perform(
            post("/payments/")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create payment session with null rental id should return 400`() {
        val requestDto = CreatePaymentSessionDto().apply {
            rentalId = null
            paymentType = "PAYMENT"
        }

        mockMvc.perform(
            post("/payments/")
                .header("Authorization", "Bearer $authToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
        )
            .andExpect(status().isBadRequest)
    }
}
