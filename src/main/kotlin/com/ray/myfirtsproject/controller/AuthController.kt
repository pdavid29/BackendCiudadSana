package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.dto.RegisterRequest
import com.ray.myfirtsproject.dto.UserResponse
import com.ray.myfirtsproject.dto.auth.LoginRequest
import com.ray.myfirtsproject.dto.auth.TokenResponse
import com.ray.myfirtsproject.service.AuthService
import com.ray.myfirtsproject.service.PasswordRecoveryService
import com.ray.myfirtsproject.dto.SolicitarRecuperacionRequest
import com.ray.myfirtsproject.dto.RestablecerContrasenaRequest
import com.ray.myfirtsproject.service.SesionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val passwordRecoveryService: PasswordRecoveryService,
    private val sesionService: SesionService
) {

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<TokenResponse> {

        return ResponseEntity.ok(authService.login(request))
    }

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): ResponseEntity<UserResponse> {

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authService.register(request))
    }

    @PostMapping("/recuperar-contrasena")
    fun solicitarRecuperacion(@Valid @RequestBody request: SolicitarRecuperacionRequest): ResponseEntity<Void> {
        passwordRecoveryService.solicitar(request.correo)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/restablecer-contrasena")
    fun restablecerContrasena(@Valid @RequestBody request: RestablecerContrasenaRequest): ResponseEntity<Void> {
        passwordRecoveryService.restablecer(request)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization", required = false) authorization: String?): ResponseEntity<Void> {
        authorization?.takeIf { it.startsWith("Bearer ") }?.removePrefix("Bearer ")?.trim()?.let(sesionService::cerrar)
        return ResponseEntity.noContent().build()
    }
}
