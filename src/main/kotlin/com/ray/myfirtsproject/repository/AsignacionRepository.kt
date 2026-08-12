package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.Asignacion
import org.springframework.data.jpa.repository.JpaRepository

interface AsignacionRepository : JpaRepository<Asignacion, Int>