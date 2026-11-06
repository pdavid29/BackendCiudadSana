package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "asignacion", schema = "public")
class Asignacion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    var idAsignacion: Int? = null,

    @Column(name = "fecha_asignacion", nullable = false)
    var fechaAsignacion: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    var updatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reporte", nullable = false)
    var reporte: Reporte,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario", nullable = false)
    var funcionario: Usuario,

    @Column(name = "estado", nullable = false)
    var estado: Boolean = true
)
