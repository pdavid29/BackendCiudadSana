package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.dto.UpdateUsuarioRequest
import com.ray.myfirtsproject.dto.UserResponse
import com.ray.myfirtsproject.model.Usuario
import com.ray.myfirtsproject.repository.UsuarioRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UsuarioService(

    private val usuarioRepository: UsuarioRepository

) {

    fun listar(): List<UserResponse> {

        return usuarioRepository.findAll().map { it.toResponse() }
    }

    fun buscarPorId(id: UUID): UserResponse {

        val usuario = usuarioRepository.findById(id)
            .orElseThrow { RuntimeException("Usuario no encontrado") }

        return usuario.toResponse()
    }

    fun actualizar(
        id: UUID,
        request: UpdateUsuarioRequest
    ): UserResponse {

        val usuario = usuarioRepository.findById(id)
            .orElseThrow { RuntimeException("Usuario no encontrado") }

        usuario.primerNombre = request.primerNombre
        usuario.segundoNombre = request.segundoNombre
        usuario.primerApellido = request.primerApellido
        usuario.segundoApellido = request.segundoApellido
        usuario.idTipoDocumento = request.idTipoDocumento
        usuario.numeroDocumento = request.numeroDocumento
        usuario.idSexo = request.idSexo
        usuario.fechaNacimiento = request.fechaNacimiento
        usuario.telefono = request.telefono
        usuario.fotoPerfil = request.fotoPerfil

        return usuarioRepository.save(usuario).toResponse()
    }

    fun eliminar(id: UUID) {

        val usuario = usuarioRepository.findById(id)
            .orElseThrow { RuntimeException("Usuario no encontrado") }

        usuarioRepository.delete(usuario)
    }

    private fun Usuario.toResponse(): UserResponse {

        return UserResponse(
            idUsuario = idUsuario!!,
            primerNombre = primerNombre,
            segundoNombre = segundoNombre,
            primerApellido = primerApellido,
            segundoApellido = segundoApellido,
            correo = correo,
            telefono = telefono,
            idRol = idRol,
            estado = estado,
            createdAt = createdAt
        )
    }
}
