package com.ray.myfirtsproject.repository // Ajusta a tu paquete

import com.ray.myfirtsproject.model.Categoria
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoriaRepository : JpaRepository<Categoria, Long> {
    fun findByEstadoTrue(): List<Categoria>
}