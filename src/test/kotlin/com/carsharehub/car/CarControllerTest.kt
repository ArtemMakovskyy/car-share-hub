package com.carsharehub.car

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class CarControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var carRepository: CarRepository

    private lateinit var responseCar: CarDto

    @BeforeEach
    fun setUp() {
        carRepository.deleteAll()
    }

    @Test
    fun `create car should return 201`() {
        val dto = CreateCarRequestDto(
            model = "Civic", brand = "Honda", type = "SEDAN",
            inventoryQuantity = 5, dailyFee = 50.0
        )
        
        val response = mockMvc.perform(
            post("/api/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isCreated)
            .andReturn()
            .response
        
        println("RESPONSE: ${response.contentAsString}")
        
        responseCar = objectMapper.readValue(response.contentAsString)
        assertEquals("Civic", responseCar.model)
        assertEquals("Honda", responseCar.brand)
        assertEquals("SEDAN", responseCar.type.name)
        assertEquals(50.0, responseCar.dailyFee)
        assertNotNull(responseCar.id)
    }

    @Test
    fun `find all cars should return 200`() {
        mockMvc.perform(get("/api/cars"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `find car by id should return 200`() {
        val savedCar = carRepository.save(
            Car(model = "Civic", brand = "Honda", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )
        
        mockMvc.perform(get("/api/cars/${savedCar.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.model").value("Civic"))
            .andExpect(jsonPath("$.brand").value("Honda"))
    }

    @Test
    fun `find non-existing car by id should return 404`() {
        mockMvc.perform(get("/api/cars/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `update car should return 200`() {
        val savedCar = carRepository.save(
            Car(model = "Civic", brand = "Honda", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )
        
        val updateDto = CreateCarRequestDto(
            model = "Civic Sport", brand = "Honda", type = "SEDAN",
            inventoryQuantity = 10, dailyFee = 60.0
        )
        
        val response = mockMvc.perform(
            put("/api/cars/${savedCar.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.model").value("Civic Sport"))
            .andExpect(jsonPath("$.dailyFee").value(60.0))
            .andReturn()
            .response
        
        responseCar = objectMapper.readValue(response.contentAsString)
        assertEquals("Civic Sport", responseCar.model)
    }

    @Test
    fun `delete car should return 204`() {
        val savedCar = carRepository.save(
            Car(model = "Civic", brand = "Honda", type = CarType.SEDAN,
                inventoryQuantity = 5, dailyFee = 50.0)
        )
        
        mockMvc.perform(delete("/api/cars/${savedCar.id}"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `delete non-existing car should return 404`() {
        mockMvc.perform(delete("/api/cars/999"))
            .andExpect(status().isNotFound)
    }
}