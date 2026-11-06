package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.ValidacionImagen
import org.springframework.data.jpa.repository.JpaRepository

interface ValidacionImagenRepository : JpaRepository<ValidacionImagen, Long> {
    fun findByEvidenciaIdEvidenciaAndEstadoTrue(idEvidencia: Int): ValidacionImagen?
    fun findAllByEstadoTrue(): List<ValidacionImagen>
}
