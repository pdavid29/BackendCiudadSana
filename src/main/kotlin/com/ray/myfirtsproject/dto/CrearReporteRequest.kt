package com.ray.myfirtsproject.dto

import java.math.BigDecimal

data class CrearReporteRequest(

    val titulo: String,

    val descripcion: String,

    val direccionReferencia: String?,

    val latitud: BigDecimal,

    val longitud: BigDecimal,

    val idCategoria: Int

)