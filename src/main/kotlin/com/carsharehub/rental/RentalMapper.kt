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

    fun toEntity(dto: CreateRentalRequestDto): Rental
}
