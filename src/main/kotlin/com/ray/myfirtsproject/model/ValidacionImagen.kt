package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "validacion_imagen", schema = "public")
class ValidacionImagen(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_validacion") var idValidacion: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_evidencia", nullable = false, columnDefinition = "integer")
    var evidencia: EvidenciaReporte,
    @Column(nullable = false, length = 30) var resultado: String,
    @Column(name = "requiere_revision_humana", nullable = false) var requiereRevisionHumana: Boolean = true,
    @Column(nullable = false) var mensaje: String,
    @Column(name = "detecciones_json", nullable = false, columnDefinition = "text") var deteccionesJson: String = "[]",
    @Column(name = "modelo_disponible", nullable = false) var modeloDisponible: Boolean = false,
    @Column(name = "umbral_confianza", nullable = false) var umbralConfianza: Double = 0.35,
    @Column(name = "created_at", nullable = false, updatable = false) var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false) var estado: Boolean = true
) {
    @PreUpdate fun touch() { updatedAt = LocalDateTime.now() }
}
