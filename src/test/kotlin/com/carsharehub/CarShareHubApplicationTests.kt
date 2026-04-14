package com.carsharehub

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = [
    "telegram.bot.enabled=false",
    "telegram.bot.username=test_bot",
    "telegram.bot.token=test_token"
])
@SpringBootTest
class CarShareHubApplicationTests {

    @Test
    fun contextLoads() {
    }

}
