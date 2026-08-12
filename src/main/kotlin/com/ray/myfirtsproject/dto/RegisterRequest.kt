package com.ray.myfirtsproject.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class RegisterRequest(

    @field:NotBlank @field:Size(max = 80) val primerNombre: String,

    val segundoNombre: String?,

    @field:NotBlank @field:Size(max = 80) val primerApellido: String,

    val segundoApellido: String?,

    @field:jakarta.validation.constraints.Positive val idTipoDocumento: Int,

    @field:NotBlank val numeroDocumento: String,

    @field:jakarta.validation.constraints.Positive val idSexo: Int,

    @field:jakarta.validation.constraints.Past val fechaNacimiento: LocalDate,

    @field:Email @field:NotBlank val correo: String,

    val telefono: String?,

    @field:Size(min = 8, max = 100) val password: String
)
