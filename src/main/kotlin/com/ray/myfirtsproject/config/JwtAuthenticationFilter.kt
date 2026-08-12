package com.ray.myfirtsproject.config

import com.ray.myfirtsproject.repository.UsuarioRepository
import com.ray.myfirtsproject.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val usuarioRepository: UsuarioRepository,
    private val sesionService: com.ray.myfirtsproject.service.SesionService
) : OncePerRequestFilter() {

    // Exime las peticiones OPTIONS (preflight) de la evaluación del Token JWT
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return "OPTIONS".equals(request.method, ignoreCase = true)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val header = request.getHeader("Authorization")

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = header.removePrefix("Bearer ").trim()
        val correo = jwtService.extractCorreo(token)

        if (
            correo != null &&
            jwtService.isTokenValid(token) &&
            !sesionService.estaRevocado(token) &&
            SecurityContextHolder.getContext().authentication == null
        ) {
            val usuario = usuarioRepository.findByCorreo(correo).orElse(null)

            if (usuario != null) {
                val authorities = listOf(
                    SimpleGrantedAuthority("ROLE_${usuario.idRol}")
                )

                val authentication = UsernamePasswordAuthenticationToken(
                    usuario.correo,
                    null,
                    authorities
                )

                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}
