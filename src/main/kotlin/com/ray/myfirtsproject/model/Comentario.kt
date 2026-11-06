package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "comentario", schema = "public")
class Comentario(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario") var idComentario: Int? = null,
    @Column(name = "comentario", nullable = false, columnDefinition = "text") var comentario: String,
    @Column(name = "fecha_comentario", nullable = false, insertable = false, updatable = false) var fechaComentario: LocalDateTime? = null,
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false) var createdAt: LocalDateTime? = null,
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false) var updatedAt: LocalDateTime? = null,
    @Column(nullable = false) var estado: Boolean = true,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_usuario", nullable = false) var usuario: Usuario,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_reporte", nullable = false) var reporte: Reporte
)
