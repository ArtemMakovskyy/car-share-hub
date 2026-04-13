package com.carsharehub.rental

import com.carsharehub.config.MapperConfig
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(config = MapperConfig::class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface RentalMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "carId", source = "car.id")
    fun toDto(rental: Rental): RentalDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "actualReturnDate", ignore = true)
    fun toEntity(dto: CreateRentalRequestDto): Rental
}
