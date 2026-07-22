package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.dto.CrearReporteRequest
import com.ray.myfirtsproject.model.Reporte
import com.ray.myfirtsproject.service.ReporteService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reportes")
class ReporteController(

    private val reporteService: ReporteService

) {

    @PostMapping
    fun crear(
        @RequestBody request: CrearReporteRequest,
        authentication: Authentication
    ): Reporte {

        val correo = authentication.name

        return reporteService.crear(
            request,
            correo
        )
    }
}