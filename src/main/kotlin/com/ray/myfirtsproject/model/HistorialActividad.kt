package com.ray.myfirtsproject.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "historial_actividad", schema = "public")
class HistorialActividad(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad") var idActividad: Long? = null,
    @Column(nullable = false, length = 100) var accion: String,
    @Column(nullable = false, length = 1200) var descripcion: String,
    @Column(name = "entidad", nullable = false, length = 80) var entidad: String,
    @Column(name = "id_entidad", length = 80) var idEntidad: String? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_usuario") var usuario: Usuario? = null,
    @Column(name = "fecha_hora", nullable = false) var fechaHora: LocalDateTime = LocalDateTime.now()
)
