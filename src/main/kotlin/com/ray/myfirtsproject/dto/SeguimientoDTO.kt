package com.ray.myfirtsproject.dto

import java.time.LocalDateTime

data class EvidenciaResponse(
    val idEvidencia: Int?,
    val nombreOriginal: String,
    val tipoContenido: String,
    val tamanio: Long,
    val fechaCarga: LocalDateTime,
    val url: String,
    val validacion: ValidacionImagenResponse? = null
)

data class ValidacionImagenResponse(
    val resultado: String,
    val requiereRevisionHumana: Boolean,
    val mensaje: String,
    val deteccionesJson: String,
    val modeloDisponible: Boolean,
    val umbralConfianza: Double
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
    val resueltos: Long,
    val totalUsuarios: Long = 0,
    val usuariosActivos: Long = 0,
    val porMes: Map<String, Long> = emptyMap(),
    val porPrioridad: Map<String, Long> = emptyMap(),
    val porBarrio: Map<String, Long> = emptyMap(),
    val evidencias: Long = 0,
    val validacionesImagen: Long = 0
)
