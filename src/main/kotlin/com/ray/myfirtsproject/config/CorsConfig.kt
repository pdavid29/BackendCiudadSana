package com.ray.myfirtsproject.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.beans.factory.annotation.Value

@Configuration
class CorsConfig(
    @param:Value("\${app.cors.allowed-origins:http://localhost:4200,http://127.0.0.1:4200,http://localhost:5500,http://127.0.0.1:5500}")
    private val allowedOrigins: String
) {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins(*allowedOrigins.split(',').map { it.trim() }.filter { it.isNotEmpty() }.toTypedArray())
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
            }
        }
    }
}
