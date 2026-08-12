package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.dto.RestablecerContrasenaRequest
import com.ray.myfirtsproject.model.PasswordResetToken
import com.ray.myfirtsproject.repository.PasswordResetTokenRepository
import com.ray.myfirtsproject.repository.UsuarioRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class PasswordRecoveryService(
    private val usuarios: UsuarioRepository,
    private val tokens: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val auditoria: AuditoriaService
) {
    /**
     * El token se persiste para integrarlo con un proveedor de correo. Por seguridad
     * la API nunca revela si el correo existe ni devuelve el token al cliente.
     */
    fun solicitar(correo: String) {
        val usuario = usuarios.findByCorreo(correo.trim().lowercase()).orElse(null) ?: return
        val token = UUID.randomUUID().toString().replace("-", "")
        tokens.save(PasswordResetToken(
            token = token,
            usuario = usuario,
            fechaExpiracion = LocalDateTime.now().plusMinutes(30)
        ))
        auditoria.registrar(usuario, "RECUPERACION_SOLICITADA", "USUARIO", usuario.idUsuario, "Se solicitó recuperación de contraseña")
        // Conecta aquí el proveedor de correo para enviar el token o enlace de restablecimiento.
    }

    fun restablecer(request: RestablecerContrasenaRequest) {
        val token = tokens.findByToken(request.token).orElseThrow { IllegalArgumentException("Token inválido") }
        if (token.usado || token.fechaExpiracion.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("Token inválido o vencido")
        }
        token.usuario.contrasena = passwordEncoder.encode(request.nuevaContrasena)
        token.usado = true
        usuarios.save(token.usuario)
        tokens.save(token)
        auditoria.registrar(token.usuario, "CONTRASENA_RESTABLECIDA", "USUARIO", token.usuario.idUsuario, "La contraseña fue restablecida")
    }
}
