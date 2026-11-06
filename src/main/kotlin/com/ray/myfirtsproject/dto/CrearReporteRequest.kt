package com.ray.myfirtsproject.dto

import java.math.BigDecimal
import jakarta.validation.constraints.*

data class CrearReporteRequest(
    @field:NotBlank @field:Size(max = 255) val titulo: String,
    @field:NotBlank @field:Size(max = 255) val descripcion: String,
    @field:Size(max = 255) val direccionReferencia: String?,
    @field:DecimalMin("-90") @field:DecimalMax("90") val latitud: BigDecimal,
    @field:DecimalMin("-180") @field:DecimalMax("180") val longitud: BigDecimal,
    @field:Positive val idCategoria: Int,
    @field:NotNull @field:Positive val idBarrio: Int,
    val idMunicipio: Int? = null,
    val idPrioridad: Int? = 2
)
