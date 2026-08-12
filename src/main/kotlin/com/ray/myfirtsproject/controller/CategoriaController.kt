package com.ray.myfirtsproject.controller

import com.ray.myfirtsproject.model.Categoria
import com.ray.myfirtsproject.service.CategoriaService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/categorias")
// La configuración de CORS ya la maneja tu SecurityConfig.kt de forma segura.
// Si deseas mantener la anotación aquí, especifica el origen directo:
// @CrossOrigin(origins = ["http://127.0.0.1:5500", "http://localhost:5500"], allowCredentials = "true")
class CategoriaController(
    private val categoriaService: CategoriaService
) {

    @GetMapping
    fun listarTodas(): ResponseEntity<List<Categoria>> {
        return ResponseEntity.ok(categoriaService.obtenerTodas())
    }

    @GetMapping("/{id}")
    fun obtenerPorId(@PathVariable id: Long): ResponseEntity<Categoria> {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id))
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun crear(@RequestBody categoria: Categoria): ResponseEntity<Categoria> {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.guardar(categoria))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun actualizar(@PathVariable id: Long, @RequestBody categoria: Categoria): ResponseEntity<Categoria> {
        return ResponseEntity.ok(categoriaService.actualizar(id, categoria))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_1')")
    fun eliminar(@PathVariable id: Long): ResponseEntity<Void> {
        categoriaService.eliminar(id)
        return ResponseEntity.noContent().build()
    }
}
