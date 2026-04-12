package com.carsharehub.config

import org.mapstruct.InjectionStrategy
import org.mapstruct.ReportingPolicy

@org.mapstruct.MapperConfig(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    implementationPackage = "com.carsharehub.dto.mapper.impl"
)
interface MapperConfig
