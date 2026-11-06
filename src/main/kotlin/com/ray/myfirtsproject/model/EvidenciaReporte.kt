package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "evidencia", schema = "public")
class EvidenciaReporte(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evidencia") var idEvidencia: Int? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_reporte", nullable = false)
    var reporte: Reporte,
    @Column(name = "nombre_archivo", nullable = false, length = 150) var nombreOriginal: String,
    @Column(name = "ruta_archivo", nullable = false, length = 255) var nombreAlmacenado: String,
    @Column(name = "tipo_archivo", nullable = false, length = 10) var tipoContenido: String,
    @Column(name = "tamano_mb", nullable = false, precision = 6, scale = 2) var tamanioMb: BigDecimal,
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false) var fechaCarga: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false) var estado: Boolean = true
)
