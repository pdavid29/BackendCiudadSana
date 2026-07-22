package com.ray.myfirtsproject.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "usuario")
data class Usuario(

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id_usuario")
    var idUsuario: UUID? = null,

    @Column(name = "primer_nombre", nullable = false)
    var primerNombre: String = "",

    @Column(name = "segundo_nombre")
    var segundoNombre: String? = null,

    @Column(name = "primer_apellido", nullable = false)
    var primerApellido: String = "",

    @Column(name = "segundo_apellido")
    var segundoApellido: String? = null,

    @Column(name = "id_tipo_documento", nullable = false)
    var idTipoDocumento: Int = 1,

    @Column(name = "numero_documento", nullable = false)
    var numeroDocumento: String = "",

    @Column(name = "id_sexo", nullable = false)
    var idSexo: Int = 1,

    @Column(name = "fecha_nacimiento", nullable = false)
    var fechaNacimiento: LocalDate,

    @Column(name = "correo", nullable = false)
    var correo: String = "",

    @Column(name = "telefono")
    var telefono: String? = null,

    @Column(name = "contrasena", nullable = false)
    var contrasena: String = "",

    @Column(name = "foto_perfil")
    var fotoPerfil: String? = null,

    @Column(name = "id_rol", nullable = false)
    var idRol: Int = 3,

    @Column(name = "estado")
    var estado: Boolean = true,

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", insertable = false, updatable = false)
    var updatedAt: LocalDateTime? = null
)