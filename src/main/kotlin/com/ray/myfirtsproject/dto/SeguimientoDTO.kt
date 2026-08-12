package com.ray.myfirtsproject.dto

import java.time.LocalDateTime

data class EvidenciaResponse(
    val idEvidencia: Long?,
    val nombreOriginal: String,
    val tipoContenido: String,
    val tamanio: Long,
    val fechaCarga: LocalDateTime,
    val url: String
)

data class HistorialEstadoResponse(
    val idHistorial: Int?,
    val idEstado: Int,
    val observacion: String?,
    val fechaCambio: LocalDateTime,
    val responsable: String
)

data class EstadisticasResponse(
    val totalReportes: Long,
    val porEstado: Map<String, Long>,
    val porCategoria: Map<String, Long>,
    val pendientes: Long,
    val resueltos: Long
)
