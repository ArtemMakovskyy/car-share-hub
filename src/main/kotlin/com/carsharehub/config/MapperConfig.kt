package com.carsharehub.config

import org.mapstruct.Builder
import org.mapstruct.InjectionStrategy
import org.mapstruct.MapperConfig
import org.mapstruct.MappingConstants
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ReportingPolicy

@MapperConfig(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    builder = Builder(disableBuilder = true),
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
interface MapperConfig
