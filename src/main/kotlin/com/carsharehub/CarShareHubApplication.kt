package com.carsharehub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.TimeZone

@SpringBootApplication
class CarShareHubApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"))
            runApplication<CarShareHubApplication>(*args)
        }
    }
}
