package com.carsharehub.payment

import com.carsharehub.car.Car
import com.carsharehub.car.CarRepository
import com.carsharehub.car.CarType
import com.carsharehub.rental.Rental
import com.carsharehub.rental.RentalRepository
import com.carsharehub.user.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class PaymentRepositoryTest {

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    @Autowired
    private lateinit var rentalRepository: RentalRepository

    @Autowired
    private lateinit var carRepository: CarRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    private lateinit var roleCustomer: Role
    private lateinit var car: Car
    private lateinit var user: User
    private lateinit var rental: Rental

    @BeforeEach
    fun setUp() {
        paymentRepository.deleteAll()
        rentalRepository.deleteAll()
        carRepository.deleteAll()
        userRepository.deleteAll()
        roleRepository.deleteAll()

        roleCustomer = roleRepository.save(Role(name = RoleName.ROLE_CUSTOMER))

        user = User().apply {
            email = "test@example.com"
            firstName = "John"
            lastName = "Doe"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer)
        }
        userRepository.save(user)

        car = Car(model = "Civic-Repo", brand = "Honda-Repo", type = CarType.SEDAN,
            inventoryQuantity = 5, dailyFee = 50.0)
        carRepository.save(car)

        rental = Rental(
            rentalDate = LocalDate.now(),
            returnDate = LocalDate.now().plusDays(7),
            car = car,
            user = user,
            isActive = true
        )
        rentalRepository.save(rental)
    }

    @Test
    fun `save payment should persist`() {
        val payment = Payment(
            sessionId = "test_session_save",
            status = PaymentStatus.PENDING,
            type = PaymentType.PAYMENT,
            rental = rental,
            amountToPay = BigDecimal("350.00"),
            sessionUrl = "https://checkout.stripe.com/test"
        )

        val saved = paymentRepository.save(payment)
        assertNotNull(saved.id)

        val found = paymentRepository.findById(saved.id!!)
        assertTrue(found.isPresent)
        assertEquals(PaymentStatus.PENDING, found.get().status)
        assertEquals(BigDecimal("350.00"), found.get().amountToPay)
    }

    @Test
    fun `findBySessionId should return payment`() {
        val payment = Payment(
            sessionId = "test_session_find",
            status = PaymentStatus.PENDING,
            type = PaymentType.PAYMENT,
            rental = rental,
            amountToPay = BigDecimal("350.00"),
            sessionUrl = "https://checkout.stripe.com/test"
        )
        paymentRepository.save(payment)

        val found = paymentRepository.findBySessionId("test_session_find")
        assertTrue(found.isPresent)
        assertEquals("test_session_find", found.get().sessionId)
    }

    @Test
    fun `findBySessionId should return empty for non-existing session`() {
        val found = paymentRepository.findBySessionId("non_existing")
        assertFalse(found.isPresent)
    }

    @Test
    fun `findAllByRentalUserId should return user payments`() {
        val payment = Payment(
            sessionId = "test_session_user",
            status = PaymentStatus.PENDING,
            type = PaymentType.PAYMENT,
            rental = rental,
            amountToPay = BigDecimal("350.00"),
            sessionUrl = "https://checkout.stripe.com/test"
        )
        paymentRepository.save(payment)

        val result = paymentRepository.findAllByRentalUserId(Pageable.unpaged(), user.id!!)
        assertEquals(1, result.content.size)
        assertEquals("test_session_user", result.content[0].sessionId)
    }

    @Test
    fun `findAllByRentalUserId should return empty for user without payments`() {
        val result = paymentRepository.findAllByRentalUserId(Pageable.unpaged(), 999L)
        assertEquals(0, result.content.size)
    }

    @Test
    fun `update payment status should persist`() {
        val payment = Payment(
            sessionId = "test_session_update",
            status = PaymentStatus.PENDING,
            type = PaymentType.PAYMENT,
            rental = rental,
            amountToPay = BigDecimal("350.00"),
            sessionUrl = "https://checkout.stripe.com/test"
        )
        val saved = paymentRepository.save(payment)

        saved.status = PaymentStatus.PAID
        paymentRepository.save(saved)

        val found = paymentRepository.findById(saved.id!!)
        assertTrue(found.isPresent)
        assertEquals(PaymentStatus.PAID, found.get().status)
    }
}
