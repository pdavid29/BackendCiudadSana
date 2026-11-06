package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.Asignacion
import org.springframework.data.jpa.repository.JpaRepository

interface AsignacionRepository : JpaRepository<Asignacion, Int> {
    fun findByReporte_IdReporteAndEstadoTrue(idReporte: Int): List<Asignacion>
    fun existsByReporte_IdReporteAndFuncionario_IdUsuarioAndEstadoTrue(idReporte: Int, idFuncionario: java.util.UUID): Boolean
}
