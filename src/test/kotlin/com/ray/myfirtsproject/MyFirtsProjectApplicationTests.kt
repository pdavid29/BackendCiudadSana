package com.ray.myfirtsproject

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MyFirtsProjectApplicationTests {

    @Autowired
    private lateinit var client: TestRestTemplate

    @Test
    fun contextLoads() {
    }

    @Test
    fun `health route responds successfully`() {
        val response = client.getForEntity("/", String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertFalse(response.body.isNullOrBlank())
    }

    @Test
    fun `profile route requires authentication`() {
        val response = client.getForEntity("/usuarios/me", String::class.java)
        assertFalse(response.statusCode.is2xxSuccessful)
    }

}
