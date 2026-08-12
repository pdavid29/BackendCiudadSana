package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.model.TokenRevocado
import com.ray.myfirtsproject.repository.TokenRevocadoRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class SesionService(private val tokens: TokenRevocadoRepository, private val jwtService: JwtService) {
    fun cerrar(token: String) {
        val expiracion = jwtService.expiracion(token) ?: return
        if (!tokens.existsByToken(token)) tokens.save(TokenRevocado(token = token,
            fechaExpiracion = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiracion.time), ZoneId.systemDefault())))
    }
    fun estaRevocado(token: String) = tokens.existsByToken(token)
}
