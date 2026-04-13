package com.carsharehub.user

import com.carsharehub.config.MapperConfig
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(config = MapperConfig::class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    fun toDto(user: User): UserResponseDto

    fun toResponseDtoList(users: List<User>): List<UserResponseDto>
}
