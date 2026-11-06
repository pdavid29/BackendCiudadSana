package com.ray.myfirtsproject.controller

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class DepartamentoCatalogoResponse(val idDepartamento: Int, val nombre: String)
data class MunicipioCatalogoResponse(val idMunicipio: Int, val nombre: String)

@RestController
@RequestMapping
class UbicacionController(private val jdbc: JdbcTemplate) {
    @GetMapping("/departamentos")
    fun departamentos(): List<DepartamentoCatalogoResponse> = jdbc.query(
        "SELECT id_departamento, nombre FROM public.departamento WHERE estado = TRUE ORDER BY nombre"
    ) { rs, _ -> DepartamentoCatalogoResponse(rs.getInt("id_departamento"), rs.getString("nombre")) }

    @GetMapping("/municipios")
    fun municipios(@RequestParam departamentoId: Int): List<MunicipioCatalogoResponse> = jdbc.query(
        """
            SELECT id_municipio, nombre
            FROM public.municipio
            WHERE id_departamento = ? AND estado = TRUE
            ORDER BY nombre
        """.trimIndent(),
        { rs, _ -> MunicipioCatalogoResponse(rs.getInt("id_municipio"), rs.getString("nombre")) },
        departamentoId
    )
}
