package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.Reporte
import org.springframework.data.jpa.repository.JpaRepository

interface ReporteRepository : JpaRepository<Reporte, Int>