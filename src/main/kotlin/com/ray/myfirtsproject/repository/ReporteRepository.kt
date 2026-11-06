package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.Reporte
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ReporteRepository : JpaRepository<Reporte, Int> {

    fun findByCiudadanoIdUsuario(idCiudadano: UUID): List<Reporte>

    @Query(
        value = """
            SELECT r.* FROM reporte r 
            INNER JOIN asignacion a ON a.id_reporte = r.id_reporte 
            WHERE a.id_funcionario = :idTecnico AND a.estado = TRUE
        """,
        nativeQuery = true
    )
    fun findReportesByTecnicoId(@Param("idTecnico") idTecnico: UUID): List<Reporte>

    // Consulta Nativa Ultra Rápida: Une reporte, asignación y usuario en 1 sola petición SQL
    @Query(
        value = """
            SELECT 
                r.id_reporte,
                r.titulo,
                r.descripcion,
                r.direccion_referencia,
                r.latitud,
                r.longitud,
                r.fecha_reporte,
                r.id_categoria,
                r.id_estado,
                r.id_prioridad,
                r.id_ciudadano,
                CONCAT(u_c.primer_nombre, ' ', u_c.primer_apellido) AS nombre_ciudadano,
                u_t.id_usuario AS id_tecnico,
                CONCAT(u_t.primer_nombre, ' ', u_t.primer_apellido) AS nombre_tecnico,
                r.id_barrio,
                b.nombre AS nombre_barrio,
                u_t.correo AS correo_tecnico
            FROM reporte r
            LEFT JOIN usuario u_c ON r.id_ciudadano = u_c.id_usuario
            LEFT JOIN asignacion a ON a.id_reporte = r.id_reporte AND a.estado = TRUE
            LEFT JOIN usuario u_t ON a.id_funcionario = u_t.id_usuario
            LEFT JOIN barrio b ON b.id_barrio = r.id_barrio
            ORDER BY r.id_reporte ASC
        """,
        nativeQuery = true
    )
    fun findAllReportesOptimizado(): List<Array<Any>>
}
