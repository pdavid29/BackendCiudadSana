package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.dto.CrearReporteRequest
import com.ray.myfirtsproject.model.Reporte
import com.ray.myfirtsproject.repository.ReporteRepository
import com.ray.myfirtsproject.repository.UsuarioRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class ReporteService(

    private val reporteRepository: ReporteRepository,

    private val usuarioRepository: UsuarioRepository

) {


    fun crear(
        request: CrearReporteRequest,
        correo: String
    ): Reporte {


        val usuario =
            usuarioRepository.findByCorreo(correo)
                .orElseThrow {
                    RuntimeException("Usuario no encontrado")
                }


        val reporte = Reporte(

            titulo = request.titulo,

            descripcion = request.descripcion,

            direccionReferencia = request.direccionReferencia,

            latitud = request.latitud,

            longitud = request.longitud,

            idCategoria = request.idCategoria,

            ciudadano = usuario
        )

        return reporteRepository.save(reporte)
    }
}