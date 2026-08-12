package com.ray.myfirtsproject.dto

import java.util.UUID

data class AsignarReporteRequest(
    val idTecnico: UUID,
    val idPrioridad: Int?
)
