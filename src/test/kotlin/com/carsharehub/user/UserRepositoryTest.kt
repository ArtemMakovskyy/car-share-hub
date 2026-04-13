package com.carsharehub.user

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    private lateinit var roleCustomer: Role
    private lateinit var roleAdmin: Role

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        roleRepository.deleteAll()
        roleCustomer = roleRepository.save(Role(name = RoleName.ROLE_CUSTOMER))
        roleAdmin = roleRepository.save(Role(name = RoleName.ROLE_ADMIN))
    }

    @Test
    fun `findUserByEmail should return user when exists`() {
        val user = User().apply {
            email = "test@example.com"
            firstName = "John"
            lastName = "Doe"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer)
        }
        userRepository.save(user)

        val found = userRepository.findUserByEmail("test@example.com")

        assertTrue(found.isPresent)
        assertEquals("test@example.com", found.get().email)
        assertEquals("John", found.get().firstName)
    }

    @Test
    fun `findUserByEmail should return empty when not found`() {
        val found = userRepository.findUserByEmail("nonexistent@example.com")
        assertFalse(found.isPresent)
    }

    @Test
    fun `findUserByEmail should not return deleted user`() {
        val user = User().apply {
            email = "deleted@example.com"
            firstName = "John"
            lastName = "Doe"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer)
            isDeleted = true
        }
        userRepository.save(user)

        val found = userRepository.findUserByEmail("deleted@example.com")
        assertFalse(found.isPresent)
    }

    @Test
    fun `findById should eagerly load roles`() {
        val user = User().apply {
            email = "withroles@example.com"
            firstName = "Jane"
            lastName = "Doe"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer, roleAdmin)
        }
        userRepository.save(user)

        val found = userRepository.findById(user.id!!)
        assertTrue(found.isPresent)
        assertEquals(2, found.get().roles.size)
    }

    @Test
    fun `findByTelegramChatId should return matching users`() {
        val chatId = 123456789L
        val user = User().apply {
            email = "telegram@example.com"
            firstName = "John"
            lastName = "Doe"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer)
            telegramChatId = chatId
        }
        userRepository.save(user)

        val found = userRepository.findByTelegramChatId(chatId)
        assertEquals(1, found.size)
        assertEquals("telegram@example.com", found[0].email)
    }

    @Test
    fun `findByRoles should return users with specified role`() {
        val user1 = User().apply {
            email = "customer1@example.com"
            firstName = "John"
            lastName = "Doe"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer)
        }
        val user2 = User().apply {
            email = "customer2@example.com"
            firstName = "Jane"
            lastName = "Smith"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer)
        }
        userRepository.saveAll(listOf(user1, user2))

        val found = userRepository.findByRoles(roleCustomer)
        assertEquals(2, found.size)
    }

    @Test
    fun `save should persist user with roles`() {
        val user = User().apply {
            email = "save@example.com"
            firstName = "Save"
            lastName = "Test"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer)
        }

        val savedUser = userRepository.save(user)
        assertNotNull(savedUser.id)

        val found = userRepository.findById(savedUser.id!!)
        assertTrue(found.isPresent)
        assertEquals("save@example.com", found.get().email)
        assertEquals(1, found.get().roles.size)
    }

    @Test
    fun `delete should soft delete user`() {
        val user = User().apply {
            email = "delete@example.com"
            firstName = "Delete"
            lastName = "Me"
            setPassword("password123")
            roles = mutableSetOf(roleCustomer)
        }
        userRepository.save(user)

        userRepository.delete(user)

        val found = userRepository.findUserByEmail("delete@example.com")
        assertFalse(found.isPresent)
    }
}
