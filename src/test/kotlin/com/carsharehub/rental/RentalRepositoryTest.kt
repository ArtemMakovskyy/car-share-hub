package com.carsharehub.rental

import com.carsharehub.car.Car
import com.carsharehub.car.CarRepository
import com.carsharehub.car.CarType
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
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class RentalRepositoryTest {

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

    @BeforeEach
    fun setUp() {
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

        car = Car(model = "Civic", brand = "Honda", type = CarType.SEDAN,
            inventoryQuantity = 5, dailyFee = 50.0)
        carRepository.save(car)
    }

    @Test
    fun `save rental should persist`() {
        val rental = Rental(
            rentalDate = LocalDate.now(),
            returnDate = LocalDate.now().plusDays(7),
            car = car,
            user = user,
            isActive = true
        )

        val saved = rentalRepository.save(rental)
        assertNotNull(saved.id)

        val found = rentalRepository.findById(saved.id!!)
        assertTrue(found.isPresent)
        assertEquals(LocalDate.now(), found.get().rentalDate)
    }

    @Test
    fun `findAllByUserIdAndActive should return user rentals`() {
        val rental = Rental(
            rentalDate = LocalDate.now(),
            returnDate = LocalDate.now().plusDays(7),
            car = car,
            user = user,
            isActive = true
        )
        rentalRepository.save(rental)

        val result = rentalRepository.findAllByUserIdAndActive(user.id!!, true, Pageable.unpaged())
        assertEquals(1, result.content.size)
        assertEquals(user.email, result.content[0].user!!.email)
    }

    @Test
    fun `findAllByUserIdAndActive should not return inactive rentals`() {
        val rental = Rental(
            rentalDate = LocalDate.now(),
            returnDate = LocalDate.now().plusDays(7),
            car = car,
            user = user,
            isActive = false
        )
        rentalRepository.save(rental)

        val result = rentalRepository.findAllByUserIdAndActive(user.id!!, true, Pageable.unpaged())
        assertEquals(0, result.content.size)
    }

    @Test
    fun `findByIsActive should return active rentals`() {
        val rental1 = Rental(
            rentalDate = LocalDate.now(),
            returnDate = LocalDate.now().plusDays(7),
            car = car,
            user = user,
            isActive = true
        )
        rentalRepository.save(rental1)

        val result = rentalRepository.findByIsActive(true, Pageable.unpaged())
        assertEquals(1, result.content.size)
    }

    @Test
    fun `soft delete should remove rental from queries`() {
        val rental = Rental(
            rentalDate = LocalDate.now(),
            returnDate = LocalDate.now().plusDays(7),
            car = car,
            user = user,
            isActive = true
        )
        rentalRepository.save(rental)

        rentalRepository.delete(rental)

        val result = rentalRepository.findAllByUserIdAndActive(user.id!!, true, Pageable.unpaged())
        assertEquals(0, result.content.size)
    }

    @Test
    fun `findAllDetailedActiveRentalsWithTelegramChatId should return rentals with telegram users`() {
        val userWithTelegram = User().apply {
            email = "telegram@example.com"
            firstName = "Telegram"
            lastName = "User"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer)
            telegramChatId = 123456789L
        }
        userRepository.save(userWithTelegram)

        val rental = Rental(
            rentalDate = LocalDate.now(),
            returnDate = LocalDate.now().plusDays(7),
            car = car,
            user = userWithTelegram,
            isActive = true
        )
        rentalRepository.save(rental)

        val result = rentalRepository.findAllDetailedActiveRentalsWithTelegramChatId()
        assertEquals(1, result.size)
        assertEquals(123456789L, result[0].user!!.telegramChatId)
    }

    @Test
    fun `findAllDetailedActiveRentalsWithTelegramChatId should not return inactive rentals`() {
        val userWithTelegram = User().apply {
            email = "inactive@example.com"
            firstName = "Inactive"
            lastName = "User"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer)
            telegramChatId = 987654321L
        }
        userRepository.save(userWithTelegram)

        val rental = Rental(
            rentalDate = LocalDate.now(),
            returnDate = LocalDate.now().plusDays(7),
            car = car,
            user = userWithTelegram,
            isActive = false
        )
        rentalRepository.save(rental)

        val result = rentalRepository.findAllDetailedActiveRentalsWithTelegramChatId()
        assertEquals(0, result.size)
    }

    @Test
    fun `findAllByUserIdAndActive should eagerly load car and user`() {
        val rental = Rental(
            rentalDate = LocalDate.now(),
            returnDate = LocalDate.now().plusDays(7),
            car = car,
            user = user,
            isActive = true
        )
        rentalRepository.save(rental)

        val result = rentalRepository.findAllByUserIdAndActive(user.id!!, true, Pageable.unpaged())
        assertEquals("Civic", result.content[0].car!!.model)
        assertEquals("test@example.com", result.content[0].user!!.email)
    }
}
