package com.ray.myfirtsproject.dto.auth

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotBlank

data class LoginRequest(

    @field:NotBlank
    @param:JsonAlias("email")
    val correo: String,

    @field:NotBlank
    @param:JsonAlias("password")
    val contrasena: String
)
