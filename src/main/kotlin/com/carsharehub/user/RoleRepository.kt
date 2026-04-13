package com.carsharehub.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(name: RoleName): Optional<Role>
}
