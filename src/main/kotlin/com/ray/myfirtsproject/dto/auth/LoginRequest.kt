package com.ray.myfirtsproject.dto.auth


import jakarta.validation.constraints.NotBlank

data class LoginRequest(

    @field:NotBlank
    val correo: String,

    @field:NotBlank
    val password: String
)
