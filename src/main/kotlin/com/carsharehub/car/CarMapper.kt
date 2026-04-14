package com.carsharehub.car

import com.carsharehub.config.MapperConfig
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Named

@Mapper(config = MapperConfig::class)
interface CarMapper {

    fun toDto(car: Car): CarDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = ["stringToCarType"])
    fun toEntity(dto: CreateCarRequestDto): Car

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = ["stringToCarType"])
    fun updateCarFromDto(dto: CreateCarRequestDto, @MappingTarget car: Car)

    companion object {
        @Named("stringToCarType")
        @JvmStatic
        fun stringToCarType(type: String?): CarType? {
            if (type == null) return null
            return CarType.entries.firstOrNull { it.name.equals(type, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid car type: $type")
        }
    }
}
