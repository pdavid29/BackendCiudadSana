package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.model.HistorialActividad
import com.ray.myfirtsproject.model.Usuario
import com.ray.myfirtsproject.repository.HistorialActividadRepository
import org.springframework.stereotype.Service

@Service
class AuditoriaService(private val repository: HistorialActividadRepository) {
    fun registrar(usuario: Usuario?, accion: String, entidad: String, idEntidad: Any?, descripcion: String) {
        repository.save(HistorialActividad(
            accion = accion,
            entidad = entidad,
            idEntidad = idEntidad?.toString(),
            descripcion = descripcion,
            usuario = usuario
        ))
    }
}
