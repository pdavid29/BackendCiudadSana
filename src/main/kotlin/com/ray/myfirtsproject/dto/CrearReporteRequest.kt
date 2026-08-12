package com.ray.myfirtsproject.dto

import java.math.BigDecimal
import jakarta.validation.constraints.*

data class CrearReporteRequest(
    @field:NotBlank @field:Size(max = 160) val titulo: String,
    @field:NotBlank @field:Size(max = 2000) val descripcion: String,
    val direccionReferencia: String?,
    @field:NotNull val latitud: BigDecimal,
    @field:NotNull val longitud: BigDecimal,
    @field:Positive val idCategoria: Int,
    val idBarrio: Int? = 1,
    val idPrioridad: Int? = 1
)
