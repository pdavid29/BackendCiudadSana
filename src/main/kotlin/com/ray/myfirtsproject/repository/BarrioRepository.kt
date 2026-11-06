package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.Barrio
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BarrioRepository : JpaRepository<Barrio, Int> {
    fun findAllByEstadoTrueOrderByNombreAsc(): List<Barrio>

    @Query(
        value = """
            SELECT b.*
            FROM public.barrio b
            JOIN public.localidad l ON l.id_localidad = b.id_localidad
            WHERE l.id_municipio = :municipioId
              AND l.estado = TRUE
              AND b.estado = TRUE
            ORDER BY b.nombre
        """,
        nativeQuery = true
    )
    fun findAllActiveByMunicipioId(@Param("municipioId") municipioId: Int): List<Barrio>
}
