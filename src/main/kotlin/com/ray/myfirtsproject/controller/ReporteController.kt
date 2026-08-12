package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.dto.*
import com.ray.myfirtsproject.service.ReporteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import jakarta.validation.Valid
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType

@RestController
@RequestMapping("/reportes")
class ReporteController(
    private val reporteService: ReporteService
) {

    // RF-04: Registrar nuevo reporte
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_3')")
    fun crear(
        @Valid @RequestBody request: CrearReporteRequest,
        authentication: Authentication
    ): ResponseEntity<ReporteResponse> {
        val correo = authentication.name
        val response = reporteService.crear(request, correo)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // RF-05 / RF-06: Ciudadano consulta sus reportes
    @GetMapping("/mis-reportes")
    @PreAuthorize("hasAuthority('ROLE_3')")
    fun misReportes(authentication: Authentication): ResponseEntity<List<ReporteResponse>> {
        return ResponseEntity.ok(reporteService.listarMisReportes(authentication.name))
    }

    // RF-07: Asistente / Admin visualizan todos los reportes
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_1', 'ROLE_2')")
    fun listarTodos(): ResponseEntity<List<ReporteResponse>> {
        return ResponseEntity.ok(reporteService.listarTodos())
    }

    // RF-10: Técnico visualiza solo sus asignaciones
    @GetMapping("/tecnico")
    @PreAuthorize("hasAuthority('ROLE_4')")
    fun listarAsignados(authentication: Authentication): ResponseEntity<List<ReporteResponse>> {
        return ResponseEntity.ok(reporteService.listarAsignadosATecnico(authentication.name))
    }

    // RF-08: Asistente / Admin asignan reporte a un técnico
    @PatchMapping("/{id}/asignar")
    @PreAuthorize("hasAnyAuthority('ROLE_1', 'ROLE_2')")
    fun asignarTecnico(
        @PathVariable id: Int,
        @RequestBody request: AsignarReporteRequest,
        authentication: Authentication
    ): ResponseEntity<ReporteResponse> {
        return ResponseEntity.ok(
            reporteService.asignarTecnico(id, request, authentication.name)
        )
    }

    // RF-09 / RF-15: Actualizar estado de reporte
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyAuthority('ROLE_1', 'ROLE_2', 'ROLE_4')")
    fun cambiarEstado(
        @PathVariable id: Int,
        @RequestBody request: CambiarEstadoRequest,
        authentication: Authentication
    ): ResponseEntity<ReporteResponse> {
        return ResponseEntity.ok(reporteService.cambiarEstado(id, request.idEstado, authentication.name))
    }

    // RF-11 / RF-12: Técnico resuelve el reporte y añade observaciones
    @PatchMapping("/{id}/resolver")
    @PreAuthorize("hasAuthority('ROLE_4')")
    fun resolver(
        @PathVariable id: Int,
        @RequestBody request: ResolverReporteRequest,
        authentication: Authentication
    ): ResponseEntity<ReporteResponse> {
        return ResponseEntity.ok(reporteService.resolverReporte(id, request, authentication.name))
    }

    @GetMapping("/{id}/historial")
    fun historial(@PathVariable id: Int, authentication: Authentication): ResponseEntity<List<HistorialEstadoResponse>> =
        ResponseEntity.ok(reporteService.historial(id, authentication.name))

    @GetMapping("/{id}/evidencias")
    fun evidencias(@PathVariable id: Int, authentication: Authentication): ResponseEntity<List<EvidenciaResponse>> =
        ResponseEntity.ok(reporteService.evidencias(id, authentication.name))

    @PostMapping("/{id}/evidencias", consumes = ["multipart/form-data"])
    @PreAuthorize("hasAuthority('ROLE_3')")
    fun cargarEvidencia(@PathVariable id: Int, @RequestParam("archivo") archivo: MultipartFile, authentication: Authentication): ResponseEntity<EvidenciaResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(reporteService.cargarEvidencia(id, archivo, authentication.name))

    @GetMapping("/evidencias/{id}/archivo")
    fun descargarEvidencia(@PathVariable id: Long, authentication: Authentication): ResponseEntity<Resource> {
        val (archivo, tipoContenido) = reporteService.archivoEvidencia(id, authentication.name)
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(tipoContenido))
            .body(InputStreamResource(java.nio.file.Files.newInputStream(archivo)))
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyAuthority('ROLE_1', 'ROLE_2')")
    fun buscar(
        @RequestParam(required = false) categoria: Int?,
        @RequestParam(required = false) estado: Int?,
        @RequestParam(required = false) fechaDesde: String?,
        @RequestParam(required = false) fechaHasta: String?,
        @RequestParam(required = false) ubicacion: String?
    ): ResponseEntity<List<ReporteResponse>> = ResponseEntity.ok(
        reporteService.buscar(categoria, estado, fechaDesde, fechaHasta, ubicacion)
    )
}
