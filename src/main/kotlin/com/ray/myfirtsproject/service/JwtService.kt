package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.model.Usuario
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(

    @param:Value("\${app.jwt.secret}")
    private val secret: String,

    @param:Value("\${app.jwt.expiration-minutes}")
    val expirationMinutes: Long

) {

    private val signingKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))
    }

    fun generateToken(usuario: Usuario): String {

        val now = Date()

        val expiration = Date(now.time + expirationMinutes * 60_000)

        return Jwts.builder()
            .subject(usuario.correo)
            .claim("rol", usuario.idRol)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(signingKey)
            .compact()
    }

    fun extractCorreo(token: String): String? {
        return extractAllClaims(token)?.subject
    }

    fun isTokenValid(token: String): Boolean {
        return try {
            extractAllClaims(token) != null && !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }

    fun expiracion(token: String): Date? = extractAllClaims(token)?.expiration

    private fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token)?.expiration?.before(Date()) ?: true
    }

    private fun extractAllClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            null
        }
    }
}
