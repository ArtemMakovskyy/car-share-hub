package com.carsharehub.payment

import com.carsharehub.config.MapperConfig
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(config = MapperConfig::class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface PaymentMapper {

    @Mapping(target = "rentalId", source = "rental.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "type", source = "type")
    fun toDto(payment: Payment): PaymentDto
}
