package com.ray.myfirtsproject.dto

data class CambiarEstadoRequest(
    val idEstado: Int,
    val observacion: String? = null
)
