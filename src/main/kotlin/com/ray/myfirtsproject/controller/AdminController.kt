package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.dto.*
import com.ray.myfirtsproject.model.Categoria
import com.ray.myfirtsproject.service.AdminService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService
) {

    // RF-13: Listar usuarios
    @GetMapping("/usuarios")
    @PreAuthorize("hasAnyAuthority('ROLE_1', 'ROLE_2')")
    fun listarUsuarios(): ResponseEntity<List<UsuarioResponse>> {
        return ResponseEntity.ok(adminService.listarUsuarios())
    }

    // RF-13: Editar usuario
    @PutMapping("/usuarios/{id}")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun actualizarUsuario(
        @PathVariable id: UUID,
        @RequestBody request: ActualizarUsuarioRequest
    ): ResponseEntity<UsuarioResponse> {
        return ResponseEntity.ok(adminService.actualizarUsuario(id, request))
    }

    // RF-13: Desactivar usuario
    @PatchMapping("/usuarios/{id}/estado")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun cambiarEstadoUsuario(
        @PathVariable id: UUID,
        @RequestParam estado: Boolean
    ): ResponseEntity<Void> {
        adminService.cambiarEstadoUsuario(id, estado)
        return ResponseEntity.noContent().build()
    }

    // RF-14: Administrar categorías
    @GetMapping("/categorias")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun listarCategorias(): ResponseEntity<List<Categoria>> {
        return ResponseEntity.ok(adminService.listarCategorias())
    }

    @PostMapping("/categorias")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun crearCategoria(@RequestBody dto: CategoriaDTO): ResponseEntity<Categoria> {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.crearCategoria(dto))
    }
}
