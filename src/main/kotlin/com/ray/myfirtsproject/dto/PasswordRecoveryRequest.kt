package com.ray.myfirtsproject.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SolicitarRecuperacionRequest(@field:Email @field:NotBlank val correo: String)
data class RestablecerContrasenaRequest(
    @field:NotBlank val token: String,
    @field:Size(min = 8, max = 100) val nuevaContrasena: String
)
