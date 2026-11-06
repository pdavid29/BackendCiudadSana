package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.repository.BarrioRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class BarrioCatalogoResponse(val idBarrio: Int, val nombre: String)

@RestController
@RequestMapping("/barrios")
class BarrioController(private val barrioRepository: BarrioRepository) {
    @GetMapping
    fun listar(@RequestParam(required = false) municipioId: Int?): List<BarrioCatalogoResponse> =
        (municipioId?.let { barrioRepository.findAllActiveByMunicipioId(it) }
            ?: barrioRepository.findAllByEstadoTrueOrderByNombreAsc())
        .mapNotNull { barrio -> barrio.idBarrio?.let { BarrioCatalogoResponse(it, barrio.nombre) } }
}
