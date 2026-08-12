package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.dto.EstadisticasResponse
import com.ray.myfirtsproject.service.ReporteService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/estadisticas")
@PreAuthorize("hasAnyAuthority('ROLE_1', 'ROLE_2')")
class EstadisticasController(private val reporteService: ReporteService) {
    @GetMapping
    fun obtener(): ResponseEntity<EstadisticasResponse> = ResponseEntity.ok(reporteService.estadisticas())
}
