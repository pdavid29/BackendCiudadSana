package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "evidencia_reporte", schema = "public")
class EvidenciaReporte(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evidencia") var idEvidencia: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_reporte", nullable = false)
    var reporte: Reporte,
    @Column(name = "nombre_original", nullable = false) var nombreOriginal: String,
    @Column(name = "nombre_almacenado", nullable = false, unique = true) var nombreAlmacenado: String,
    @Column(name = "tipo_contenido", nullable = false) var tipoContenido: String,
    @Column(nullable = false) var tamanio: Long,
    @Column(name = "fecha_carga", nullable = false) var fechaCarga: LocalDateTime = LocalDateTime.now()
)
