package com.carsharehub.user

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {

    @Query("FROM User u LEFT JOIN FETCH u.roles r WHERE u.email = :email AND u.isDeleted = FALSE AND r.isDeleted = FALSE")
    fun findUserByEmail(email: String): Optional<User>

    @EntityGraph(attributePaths = ["roles"])
    override fun findById(id: Long): Optional<User>

    fun findByTelegramChatId(chatId: Long): List<User>

    @EntityGraph(attributePaths = ["roles"])
    fun findByRoles(role: Role): List<User>
}
