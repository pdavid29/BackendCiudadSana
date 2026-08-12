package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.EvidenciaReporte
import org.springframework.data.jpa.repository.JpaRepository

interface EvidenciaReporteRepository : JpaRepository<EvidenciaReporte, Long> {
    fun findByReporteIdReporte(idReporte: Int): List<EvidenciaReporte>
}
