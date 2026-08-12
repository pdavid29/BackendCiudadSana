package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "historial_estado", schema = "public")
class HistorialEstado(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    var idHistorial: Int? = null,

    @Column(name = "fecha_cambio", nullable = false)
    var fechaCambio: LocalDateTime = LocalDateTime.now(),

    var observacion: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reporte", nullable = false)
    var reporte: Reporte,

    @Column(name = "id_estado", nullable = false)
    var idEstado: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario", nullable = false)
    var funcionario: Usuario
)