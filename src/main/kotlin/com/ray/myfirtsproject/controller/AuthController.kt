package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.dto.RegisterRequest
import com.ray.myfirtsproject.dto.UserResponse
import com.ray.myfirtsproject.dto.auth.LoginRequest
import com.ray.myfirtsproject.dto.auth.TokenResponse
import com.ray.myfirtsproject.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
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
}