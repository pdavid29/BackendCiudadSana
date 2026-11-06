package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.dto.UpdateUsuarioRequest
import com.ray.myfirtsproject.dto.UserResponse
import com.ray.myfirtsproject.service.UsuarioService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID
import org.springframework.security.core.Authentication
import jakarta.validation.Valid
import org.springframework.web.multipart.MultipartFile
import org.springframework.http.MediaType
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource

@RestController
@RequestMapping("/usuarios")
class UsuarioController(
    private val usuarioService: UsuarioService
) {

    @GetMapping("/me")
    fun miPerfil(authentication: Authentication): UserResponse =
        usuarioService.buscarPorCorreo(authentication.name)

    @GetMapping("/me/modulos")
    fun misModulos(authentication: Authentication): List<com.ray.myfirtsproject.dto.ModuloAccesoResponse> =
        usuarioService.modulos(authentication.name)

    @PatchMapping("/me")
    fun actualizarMiPerfil(
        @Valid @RequestBody request: com.ray.myfirtsproject.dto.ActualizarMiPerfilRequest,
        authentication: Authentication
    ): UserResponse = usuarioService.actualizarMiPerfil(authentication.name, request)

    @PostMapping("/me/foto", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun guardarFotoPerfil(@RequestParam("archivo") archivo: MultipartFile, authentication: Authentication): UserResponse =
        usuarioService.guardarFotoPerfil(authentication.name, archivo)

    @GetMapping("/me/foto")
    fun fotoPerfil(authentication: Authentication): ResponseEntity<Resource> {
        val (file, contentType) = usuarioService.archivoFotoPerfil(authentication.name)
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
            .body(InputStreamResource(java.nio.file.Files.newInputStream(file)))
    }

    @GetMapping("/{id}/foto")
    @PreAuthorize("hasAnyAuthority('ROLE_1', 'ROLE_2')")
    fun fotoUsuario(@PathVariable id: UUID): ResponseEntity<Resource> {
        val (file, contentType) = usuarioService.archivoFotoPerfil(id)
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
            .body(InputStreamResource(java.nio.file.Files.newInputStream(file)))
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_1', 'ROLE_2')")
    fun listar(): List<UserResponse> {
        return usuarioService.listar()
    }

    @GetMapping("/tecnicos")
    @PreAuthorize("hasAnyAuthority('ROLE_1', 'ROLE_2')")
    fun listarTecnicos(): List<com.ray.myfirtsproject.dto.TecnicoResponse> = usuarioService.listarTecnicos()

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
