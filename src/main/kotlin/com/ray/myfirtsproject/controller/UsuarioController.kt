package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.dto.UpdateUsuarioRequest
import com.ray.myfirtsproject.dto.UserResponse
import com.ray.myfirtsproject.service.UsuarioService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@RestController
@RequestMapping("/usuarios")
class UsuarioController(
    private val usuarioService: UsuarioService
) {

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun listar(): List<UserResponse> {
        return usuarioService.listar()
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun buscarPorId(
        @PathVariable id: UUID
    ): UserResponse {
        return usuarioService.buscarPorId(id)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun actualizar(
        @PathVariable id: UUID,
        @RequestBody request: UpdateUsuarioRequest
    ): UserResponse {
        return usuarioService.actualizar(id, request)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun eliminar(
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        usuarioService.eliminar(id)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun cambiarEstadoUsuario(
        @PathVariable id: UUID,
        @RequestParam activo: Boolean
    ): ResponseEntity<Void> {
        usuarioService.cambiarEstado(id, activo)
        return ResponseEntity.noContent().build()
    }
}
