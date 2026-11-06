package com.ray.myfirtsproject.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate

data class RegisterRequest(

    @field:NotBlank @field:Size(max = 255) val primerNombre: String,

    @field:Size(max = 255) val segundoNombre: String? = null,

    @field:NotBlank @field:Size(max = 255) val primerApellido: String,

    @field:Size(max = 255) val segundoApellido: String? = null,

    @field:jakarta.validation.constraints.Positive val idTipoDocumento: Int,

    @field:NotBlank @field:Pattern(regexp = "^[0-9]{5,20}$") val numeroDocumento: String,

    @field:jakarta.validation.constraints.Positive val idSexo: Int,

    @field:PastOrPresent val fechaNacimiento: LocalDate,

    @field:Email @field:NotBlank @field:Size(max = 255) val correo: String,

    @field:Pattern(regexp = "^[0-9]{7,15}$") val telefono: String? = null,

    @field:Size(min = 8, max = 100) val password: String
)
