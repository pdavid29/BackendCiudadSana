package com.ray.myfirtsproject.dto

import java.time.LocalDateTime
import java.util.UUID

data class UserResponse(

    val idUsuario: UUID,

    val primerNombre: String,

    val segundoNombre: String?,

    val primerApellido: String,

    val segundoApellido: String?,

    val correo: String,

    val fotoPerfil: String?,

    val telefono: String?,

    val idRol: Int,

    val estado: Boolean,

    val createdAt: LocalDateTime?
)

data class TecnicoResponse(
    val idUsuario: java.util.UUID,
    val nombre: String
)
