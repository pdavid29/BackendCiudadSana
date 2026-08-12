package com.ray.myfirtsproject.model

import jakarta.persistence.*

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
    var descripcion: String? = null
)