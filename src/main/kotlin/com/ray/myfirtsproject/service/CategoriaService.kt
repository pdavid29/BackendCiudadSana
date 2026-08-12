package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.model.Categoria
import com.ray.myfirtsproject.repository.CategoriaRepository
import org.springframework.stereotype.Service

@Service
class CategoriaService(
    private val categoriaRepository: CategoriaRepository
) {
    fun obtenerTodas(): List<Categoria> = categoriaRepository.findAll()

    fun obtenerPorId(id: Long): Categoria = categoriaRepository.findById(id)
        .orElseThrow { RuntimeException("Categoría no encontrada con ID: $id") }

    fun guardar(categoria: Categoria): Categoria = categoriaRepository.save(categoria)

    fun actualizar(id: Long, datos: Categoria): Categoria {
        val existente = obtenerPorId(id)
        existente.nombre = datos.nombre
        existente.descripcion = datos.descripcion
        existente.icono = datos.icono
        if (datos.estado != null) {
            existente.estado = datos.estado
        }
        return categoriaRepository.save(existente)
    }

    fun eliminar(id: Long) {
        categoriaRepository.deleteById(id)
    }
}