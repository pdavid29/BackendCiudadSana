package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "password_reset_token", schema = "public")
class PasswordResetToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token") var idToken: Long? = null,
    @Column(nullable = false, unique = true, length = 100) var token: String,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_usuario", nullable = false) var usuario: Usuario,
    @Column(name = "fecha_expiracion", nullable = false) var fechaExpiracion: LocalDateTime,
    @Column(nullable = false) var usado: Boolean = false,
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false) var createdAt: LocalDateTime? = null,
    @Column(name = "updated_at", nullable = false, insertable = false) var updatedAt: LocalDateTime? = null,
    @Column(nullable = false) var estado: Boolean = true
) {
    @PreUpdate fun touchUpdatedAt() { updatedAt = LocalDateTime.now() }
}
