package com.ray.myfirtsproject.dto

import java.time.LocalDate
import java.util.UUID

data class UsuarioResponse(
    val idUsuario: UUID?,
    val primerNombre: String,
    val primerApellido: String,
    val correo: String,
    val numeroDocumento: String,
    val idRol: Int,
    val estado: Boolean
)

data class ActualizarUsuarioRequest(
    val primerNombre: String?,
    val primerApellido: String?,
    val telefono: String?,
    val idRol: Int?,
    val estado: Boolean?
)

data class CategoriaDTO(
    val idCategoria: Int? = null,
    val nombre: String,
    val descripcion: String?,
    val icono: String?
)