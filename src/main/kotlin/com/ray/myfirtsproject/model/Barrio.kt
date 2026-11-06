package com.ray.myfirtsproject.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "barrio", schema = "public")
class Barrio(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_barrio")
    var idBarrio: Int? = null,

    @Column(nullable = false, length = 255)
    var nombre: String = "",

    @Column(name = "id_localidad", nullable = false)
    var idLocalidad: Int = 1,

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    var updatedAt: LocalDateTime? = null,

    @Column(name = "estado", nullable = false)
    var estado: Boolean = true
)
