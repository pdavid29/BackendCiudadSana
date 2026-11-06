package com.ray.myfirtsproject.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class ReporteResponse(
    val idReporte: Int?,
    val titulo: String,
    val descripcion: String,
    val direccionReferencia: String?,
    val latitud: BigDecimal,
    val longitud: BigDecimal,
    val fechaReporte: LocalDateTime,
    val idCategoria: Int,
    val idEstado: Int,
    val idPrioridad: Int = 1,
    val idCiudadano: UUID?,
    val nombreCiudadano: String,
    val idTecnico: UUID? = null,
    val nombreTecnico: String? = null,
    val evidencias: List<EvidenciaResponse> = emptyList(),
    val idBarrio: Int? = null,
    val nombreBarrio: String? = null,
    val correoTecnico: String? = null
)
