package com.ray.myfirtsproject.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class RegisterRequest(

    val primerNombre: String,

    val segundoNombre: String?,

    val primerApellido: String,

    val segundoApellido: String?,

    val idTipoDocumento: Int,

    val numeroDocumento: String,

    val idSexo: Int,

    val fechaNacimiento: LocalDate,

    val correo: String,

    val telefono: String?,

    val password: String
)