package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "reporte", schema = "public")
class Reporte(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    var idReporte: Int? = null,

    @Column(nullable = false)
    var titulo: String = "",

    @Column(nullable = false)
    var descripcion: String = "",

    @Column(name = "direccion_referencia")
    var direccionReferencia: String? = null,

    @Column(nullable = false, precision = 10, scale = 7)
    var latitud: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 10, scale = 7)
    var longitud: BigDecimal = BigDecimal.ZERO,

    @Column(name = "fecha_reporte", nullable = false)
    var fechaReporte: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false, insertable = false)
    var updatedAt: LocalDateTime? = null,

    @Column(name = "id_categoria", nullable = false)
    var idCategoria: Int = 1,

    @Column(name = "id_barrio", nullable = false)
    var idBarrio: Int = 1,

    @Column(name = "id_prioridad", nullable = false)
    var idPrioridad: Int = 1,

    @Column(name = "id_estado", nullable = false)
    var idEstado: Int = 1,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciudadano", nullable = false)
    var ciudadano: Usuario? = null,

    @Column(name = "observaciones_cierre")
    var observacionesCierre: String? = null,

    @Column(name = "estado", nullable = false)
    var estado: Boolean = true
) {
    @PreUpdate
    fun touchUpdatedAt() { updatedAt = LocalDateTime.now() }
}
