package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "token_revocado", schema = "public")
class TokenRevocado(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id_token_revocado") var id: Long? = null,
    @Column(nullable = false, unique = true, length = 1000) var token: String,
    @Column(name = "fecha_expiracion", nullable = false) var fechaExpiracion: LocalDateTime,
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false) var createdAt: LocalDateTime? = null,
    @Column(name = "updated_at", nullable = false, insertable = false) var updatedAt: LocalDateTime? = null,
    @Column(nullable = false) var estado: Boolean = true
)
