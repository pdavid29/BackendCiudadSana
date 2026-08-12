package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.HistorialActividad
import org.springframework.data.jpa.repository.JpaRepository

interface HistorialActividadRepository : JpaRepository<HistorialActividad, Long>
