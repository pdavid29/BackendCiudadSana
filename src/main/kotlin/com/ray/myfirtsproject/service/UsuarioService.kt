package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.dto.UpdateUsuarioRequest
import com.ray.myfirtsproject.dto.UserResponse
import com.ray.myfirtsproject.dto.ActualizarMiPerfilRequest
import com.ray.myfirtsproject.model.Usuario
import com.ray.myfirtsproject.repository.UsuarioRepository
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID
import org.springframework.jdbc.core.JdbcTemplate

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val jdbc: JdbcTemplate,
    @Value("\${app.upload-dir:uploads}") private val uploadDir: String
) {

    fun guardarFotoPerfil(correo: String, archivo: MultipartFile): UserResponse {
        val extension = extensionFotografia(archivo)
        val usuario = usuarioRepository.findByCorreo(correo).orElseThrow { RuntimeException("Usuario no encontrado") }
        val root = Path.of(uploadDir).toAbsolutePath().normalize()
        val directory = root.resolve("perfiles").normalize()
        Files.createDirectories(directory)
        val stored = "perfiles/${usuario.idUsuario}-${UUID.randomUUID()}.$extension"
        val destination = root.resolve(stored).normalize()
        require(destination.startsWith(root)) { "Ruta de fotografía no válida" }
        archivo.inputStream.use { Files.copy(it, destination, StandardCopyOption.REPLACE_EXISTING) }
        usuario.fotoPerfil = stored
        val saved = usuarioRepository.save(usuario)
        return saved.toResponse()
    }

    fun validarFotoPerfil(archivo: MultipartFile) {
        extensionFotografia(archivo)
    }

    private fun extensionFotografia(archivo: MultipartFile): String {
        require(!archivo.isEmpty) { "Selecciona una fotografía" }
        require(archivo.size <= 5 * 1024 * 1024) { "La fotografía no puede superar 5 MB" }
        return when (archivo.contentType?.lowercase()?.substringBefore(';')) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> throw IllegalArgumentException("Selecciona una imagen JPG, PNG o WebP")
        }
    }

    fun archivoFotoPerfil(correo: String): Pair<Path, String> {
        val usuario = usuarioRepository.findByCorreo(correo).orElseThrow { RuntimeException("Usuario no encontrado") }
        return archivoFotoPerfil(usuario)
    }

    fun archivoFotoPerfil(id: UUID): Pair<Path, String> {
        val usuario = usuarioRepository.findById(id).orElseThrow { RuntimeException("Usuario no encontrado") }
        return archivoFotoPerfil(usuario)
    }

    private fun archivoFotoPerfil(usuario: Usuario): Pair<Path, String> {
        val relative = usuario.fotoPerfil ?: throw NoSuchElementException("El perfil aún no tiene fotografía")
        val root = Path.of(uploadDir).toAbsolutePath().normalize()
        val file = root.resolve(relative).normalize()
        require(file.startsWith(root) && Files.exists(file)) { "Fotografía no encontrada" }
        val type = when (file.fileName.toString().substringAfterLast('.', "").lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }
        return file to type
    }

    fun listarTecnicos(): List<com.ray.myfirtsproject.dto.TecnicoResponse> = usuarioRepository.findAll()
        .filter { it.idRol == 4 && it.estado }
        .map { usuario ->
            com.ray.myfirtsproject.dto.TecnicoResponse(
                idUsuario = usuario.idUsuario!!,
                nombre = "${usuario.primerNombre} ${usuario.primerApellido}".trim()
            )
        }

    fun buscarPorCorreo(correo: String): UserResponse = usuarioRepository.findByCorreo(correo)
        .orElseThrow { RuntimeException("Usuario no encontrado") }
        .toResponse()

    fun actualizarPerfil(correo: String, request: UpdateUsuarioRequest): UserResponse {
        val usuario = usuarioRepository.findByCorreo(correo)
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

    fun actualizarMiPerfil(correo: String, request: ActualizarMiPerfilRequest): UserResponse {
        val usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow { RuntimeException("Usuario no encontrado") }
        val parts = request.nombre.trim().split(Regex("\\s+"), limit = 2)
        usuario.primerNombre = parts.first()
        usuario.primerApellido = parts.getOrElse(1) { usuario.primerApellido }
        usuario.telefono = request.telefono?.trim()?.takeIf { it.isNotEmpty() }
        return usuarioRepository.save(usuario).toResponse()
    }

    fun listar(): List<UserResponse> {
        return usuarioRepository.findAll().map { it.toResponse() }
    }

    fun modulos(correo: String): List<com.ray.myfirtsproject.dto.ModuloAccesoResponse> {
        val user = usuarioRepository.findByCorreo(correo).orElseThrow { RuntimeException("Usuario no encontrado") }
        return jdbc.query(
            "select m.id_modulo, m.nombre, m.url from public.rol_modulo rm join public.modulo m on m.id_modulo = rm.id_modulo where rm.id_rol = ? and rm.estado = true and m.estado = true order by m.id_modulo",
            { rs, _ -> com.ray.myfirtsproject.dto.ModuloAccesoResponse(rs.getInt(1), rs.getString(2), rs.getString(3)) },
            user.idRol
        )
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
        usuario.estado = false
        usuarioRepository.save(usuario)
    }

    // 📌 Método para cambiar el estado (activar / desactivar)
    fun cambiarEstado(id: UUID, estado: Boolean): UserResponse {
        val usuario = usuarioRepository.findById(id)
            .orElseThrow { RuntimeException("Usuario no encontrado") }

        usuario.estado = estado
        return usuarioRepository.save(usuario).toResponse()
    }

    private fun Usuario.toResponse(): UserResponse {
        return UserResponse(
            idUsuario = idUsuario!!,
            primerNombre = primerNombre,
            segundoNombre = segundoNombre,
            primerApellido = primerApellido,
            segundoApellido = segundoApellido,
            correo = correo,
            fotoPerfil = fotoPerfil,
            telefono = telefono,
            idRol = idRol,
            estado = estado,
            createdAt = createdAt
        )
    }
}
