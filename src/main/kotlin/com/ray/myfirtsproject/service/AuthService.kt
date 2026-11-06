package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.dto.RegisterRequest
import com.ray.myfirtsproject.dto.UserResponse
import com.ray.myfirtsproject.dto.auth.LoginRequest
import com.ray.myfirtsproject.dto.auth.TokenResponse
import com.ray.myfirtsproject.exception.InvalidCredentialsException
import com.ray.myfirtsproject.model.Usuario
import com.ray.myfirtsproject.repository.UsuarioRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(

    private val usuarioRepository: UsuarioRepository,

    private val passwordEncoder: PasswordEncoder,

    private val jwtService: JwtService

) {

    fun login(request: LoginRequest): TokenResponse {
        val correo = request.correo.trim().lowercase()

        val usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow {
                InvalidCredentialsException("Correo o contraseña incorrectos")
            }

        if (!usuario.estado) {
            throw InvalidCredentialsException("Usuario inactivo")
        }
        
        if (!passwordEncoder.matches(request.contrasena, usuario.contrasena)) {
            throw InvalidCredentialsException("Correo o contraseña incorrectos")
        }

        val token = jwtService.generateToken(usuario)

        return TokenResponse(
            accessToken = token,
            expiresIn = jwtService.expirationMinutes * 60,
            idRol = usuario.idRol
        )

    }

    fun register(request: RegisterRequest): UserResponse {

        val correo = request.correo.trim().lowercase()

        if (usuarioRepository.existsByCorreo(correo)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "El correo ya existe"
            )
        }

        val numeroDocumento = request.numeroDocumento.trim()
        if (usuarioRepository.existsByNumeroDocumento(numeroDocumento)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "El número de documento ya está registrado"
            )
        }

        val telefono = request.telefono?.trim()?.takeIf { it.isNotEmpty() }
        if (telefono != null && usuarioRepository.existsByTelefono(telefono)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "El teléfono ya está registrado")
        }

        val usuario = Usuario(

            primerNombre = request.primerNombre.trim(),

            segundoNombre = request.segundoNombre?.trim()?.takeIf { it.isNotEmpty() },

            primerApellido = request.primerApellido.trim(),

            segundoApellido = request.segundoApellido?.trim()?.takeIf { it.isNotEmpty() },

            idTipoDocumento = request.idTipoDocumento,

            numeroDocumento = numeroDocumento,

            idSexo = request.idSexo,

            fechaNacimiento = request.fechaNacimiento,

            correo = correo,

            telefono = telefono,

            contrasena = passwordEncoder.encode(request.password)!!,

            idRol = 3,

            estado = true
        )

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
