package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.HistorialEstado
import org.springframework.data.jpa.repository.JpaRepository

interface HistorialEstadoRepository : JpaRepository<HistorialEstado, Int> {
    fun findByReporteIdReporteOrderByFechaCambioAsc(idReporte: Int): List<HistorialEstado>
}
