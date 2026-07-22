package com.ray.myfirtsproject.dto
import java.time.LocalDate

data class UpdateUsuarioRequest(
    val primerNombre: String,
    val segundoNombre: String?,
    val primerApellido: String,
    val segundoApellido: String?,
    val idTipoDocumento: Int,
    val numeroDocumento: String,
    val idSexo: Int,
    val fechaNacimiento: LocalDate,
    val telefono: String?,
    val fotoPerfil: String?
)
