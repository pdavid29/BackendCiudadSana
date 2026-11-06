package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "estado_reporte", schema = "public")
class EstadoReporte(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    var idEstado: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    var nombre: String = "",

    @Column(length = 255)
    var descripcion: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var estado: Boolean = true
) {
    @PreUpdate
    fun touchUpdatedAt() { updatedAt = LocalDateTime.now() }
}
