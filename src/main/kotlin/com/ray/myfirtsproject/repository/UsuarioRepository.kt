package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UsuarioRepository : JpaRepository<Usuario, UUID> {

    fun findByCorreo(correo: String): Optional<Usuario>

    fun existsByCorreo(correo: String): Boolean

    fun existsByNumeroDocumento(numeroDocumento: String): Boolean

    fun existsByTelefono(telefono: String): Boolean

    fun countByEstadoTrue(): Long

}
