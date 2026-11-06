package com.ray.myfirtsproject.controller

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CatalogoRegistroItem(val id: Int, val nombre: String)
data class CatalogosRegistroResponse(val tiposDocumento: List<CatalogoRegistroItem>, val sexos: List<CatalogoRegistroItem>)

@RestController
@RequestMapping("/catalogos/registro")
class CatalogoRegistroController(private val jdbc: JdbcTemplate) {
    @GetMapping
    fun listar(): CatalogosRegistroResponse {
        val tipos = jdbc.query("select id_tipo_documento, nombre from public.tipo_documento where estado = true order by id_tipo_documento") { rs, _ ->
            CatalogoRegistroItem(rs.getInt(1), rs.getString(2))
        }
        val sexos = jdbc.query("select id_sexo, nombre from public.sexo where estado = true order by id_sexo") { rs, _ ->
            CatalogoRegistroItem(rs.getInt(1), rs.getString(2))
        }
        return CatalogosRegistroResponse(tipos, sexos)
    }
}
