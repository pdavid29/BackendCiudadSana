package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.dto.*
import com.ray.myfirtsproject.model.Categoria
import com.ray.myfirtsproject.repository.CategoriaRepository
import com.ray.myfirtsproject.repository.UsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminService(
    private val usuarioRepository: UsuarioRepository,
    private val categoriaRepository: CategoriaRepository
) {

    // RF-13: Gestión de usuarios
    fun listarUsuarios(): List<UsuarioResponse> {
        return usuarioRepository.findAll().map { u ->
            UsuarioResponse(
                idUsuario = u.idUsuario,
                primerNombre = u.primerNombre,
                primerApellido = u.primerApellido,
                correo = u.correo,
                numeroDocumento = u.numeroDocumento,
                idRol = u.idRol,
                estado = u.estado
            )
        }
    }

    @Transactional
    fun actualizarUsuario(id: UUID, request: ActualizarUsuarioRequest): UsuarioResponse {
        val u = usuarioRepository.findById(id)
            .orElseThrow { RuntimeException("Usuario no encontrado") }

        request.primerNombre?.let { u.primerNombre = it }
        request.primerApellido?.let { u.primerApellido = it }
        request.telefono?.let { u.telefono = it }
        request.idRol?.let { u.idRol = it }
        request.estado?.let { u.estado = it }

        val actualizado = usuarioRepository.save(u)
        return UsuarioResponse(
            idUsuario = actualizado.idUsuario,
            primerNombre = actualizado.primerNombre,
            primerApellido = actualizado.primerApellido,
            correo = actualizado.correo,
            numeroDocumento = actualizado.numeroDocumento,
            idRol = actualizado.idRol,
            estado = actualizado.estado
        )
    }

    @Transactional
    fun cambiarEstadoUsuario(id: UUID, estado: Boolean) {
        val u = usuarioRepository.findById(id)
            .orElseThrow { RuntimeException("Usuario no encontrado") }
        u.estado = estado
        usuarioRepository.save(u)
    }

    // RF-14: Gestión de categorías
    fun listarCategorias(): List<Categoria> {
        return categoriaRepository.findAll()
    }

    @Transactional
    fun crearCategoria(dto: CategoriaDTO): Categoria {
        val cat = Categoria(
            nombre = dto.nombre,
            descripcion = dto.descripcion,
            icono = dto.icono
        )
        return categoriaRepository.save(cat)
    }
}