package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.EstadoReporte
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EstadoReporteRepository : JpaRepository<EstadoReporte, Long>