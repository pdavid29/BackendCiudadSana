package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.model.EstadoReporte
import com.ray.myfirtsproject.repository.EstadoReporteRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/estados")
class EstadoReporteController(
    private val estadoRepository: EstadoReporteRepository
) {
    @GetMapping
    fun listarTodos(): ResponseEntity<List<EstadoReporte>> =
        ResponseEntity.ok(estadoRepository.findAll())

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun crear(@RequestBody estado: EstadoReporte): ResponseEntity<EstadoReporte> =
        ResponseEntity.ok(estadoRepository.save(estado))

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun actualizar(@PathVariable id: Long, @RequestBody datos: EstadoReporte): ResponseEntity<EstadoReporte> {
        val estado = estadoRepository.findById(id).orElseThrow { IllegalArgumentException("Estado no encontrado") }
        estado.nombre = datos.nombre
        estado.descripcion = datos.descripcion
        return ResponseEntity.ok(estadoRepository.save(estado))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun eliminar(@PathVariable id: Long): ResponseEntity<Void> {
        estadoRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
