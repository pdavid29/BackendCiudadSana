package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.dto.UpdateUsuarioRequest
import com.ray.myfirtsproject.dto.UserResponse
import com.ray.myfirtsproject.service.UsuarioService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/usuarios")
class UsuarioController(

    private val usuarioService: UsuarioService

) {

    @GetMapping
    fun listar(): List<UserResponse> {

        return usuarioService.listar()
    }

    @GetMapping("/{id}")
    fun buscarPorId(

        @PathVariable id: UUID

    ): UserResponse {

        return usuarioService.buscarPorId(id)
    }

    @PutMapping("/{id}")
    fun actualizar(

        @PathVariable id: UUID,

        @RequestBody request: UpdateUsuarioRequest

    ): UserResponse {

        return usuarioService.actualizar(id, request)
    }

    @DeleteMapping("/{id}")
    fun eliminar(

        @PathVariable id: UUID

    ): ResponseEntity<Void> {

        usuarioService.eliminar(id)

        return ResponseEntity.noContent().build()
    }
}