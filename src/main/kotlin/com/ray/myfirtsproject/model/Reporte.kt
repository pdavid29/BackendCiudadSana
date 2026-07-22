package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "reporte")
data class Reporte(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    var idReporte: Int? = null,

    @Column(nullable = false, length = 100)
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

    @Column(name = "id_categoria", nullable = false)
    var idCategoria: Int = 1,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "id_ciudadano",
        nullable = false
    )
    var ciudadano: Usuario? = null

)