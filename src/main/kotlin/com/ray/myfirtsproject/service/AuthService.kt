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

        if (!passwordEncoder.matches(request.password, usuario.contrasena)) {
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

        val usuario = Usuario(

            primerNombre = request.primerNombre,

            segundoNombre = request.segundoNombre,

            primerApellido = request.primerApellido,

            segundoApellido = request.segundoApellido,

            idTipoDocumento = request.idTipoDocumento,

            numeroDocumento = request.numeroDocumento,

            idSexo = request.idSexo,

            fechaNacimiento = request.fechaNacimiento,

            correo = correo,

            telefono = request.telefono,

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

            telefono = telefono,

            idRol = idRol,

            estado = estado,

            createdAt = createdAt
        )
    }
}