package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.dto.*
import com.ray.myfirtsproject.model.Asignacion
import com.ray.myfirtsproject.model.Comentario
import com.ray.myfirtsproject.model.HistorialEstado
import com.ray.myfirtsproject.model.Reporte
import com.ray.myfirtsproject.model.Usuario
import com.ray.myfirtsproject.repository.AsignacionRepository
import com.ray.myfirtsproject.repository.ComentarioRepository
import com.ray.myfirtsproject.repository.EvidenciaReporteRepository
import com.ray.myfirtsproject.repository.HistorialEstadoRepository
import com.ray.myfirtsproject.repository.ReporteRepository
import com.ray.myfirtsproject.repository.UsuarioRepository
import com.ray.myfirtsproject.repository.CategoriaRepository
import com.ray.myfirtsproject.repository.EstadoReporteRepository
import com.ray.myfirtsproject.repository.ValidacionImagenRepository
import com.ray.myfirtsproject.repository.BarrioRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDate
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID
import org.springframework.jdbc.core.JdbcTemplate
import java.time.format.DateTimeFormatter

@Service
class ReporteService(
    private val reporteRepository: ReporteRepository,
    private val usuarioRepository: UsuarioRepository,
    private val asignacionRepository: AsignacionRepository,
    private val comentarioRepository: ComentarioRepository,
    private val historialEstadoRepository: HistorialEstadoRepository,
    private val evidenciaRepository: EvidenciaReporteRepository,
    private val categoriaRepository: CategoriaRepository,
    private val estadoRepository: EstadoReporteRepository,
    private val validacionImagenRepository: ValidacionImagenRepository,
    private val barrioRepository: BarrioRepository,
    private val jdbc: JdbcTemplate,
    private val visionClient: VisionClient,
    private val auditoria: AuditoriaService,
    @Value("\${app.upload-dir:uploads}") private val uploadDir: String
) {

    @Transactional
    fun crear(request: CrearReporteRequest, correo: String): ReporteResponse {
        val usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow { RuntimeException("Usuario no encontrado") }

        require(categoriaRepository.findById(request.idCategoria.toLong()).orElse(null)?.estado == true) { "La categoría seleccionada no está disponible" }
        require(barrioRepository.findById(request.idBarrio).orElse(null)?.estado == true) { "El barrio seleccionado no está disponible" }
        request.idMunicipio?.let { municipioId ->
            val perteneceAlMunicipio = jdbc.queryForObject(
                """
                    SELECT EXISTS (
                        SELECT 1
                        FROM public.barrio b
                        JOIN public.localidad l ON l.id_localidad = b.id_localidad
                        JOIN public.municipio m ON m.id_municipio = l.id_municipio
                        WHERE b.id_barrio = ?
                          AND l.id_municipio = ?
                          AND b.estado = TRUE
                          AND l.estado = TRUE
                          AND m.estado = TRUE
                    )
                """.trimIndent(),
                Boolean::class.java,
                request.idBarrio,
                municipioId
            ) ?: false
            require(perteneceAlMunicipio) { "El barrio seleccionado no pertenece al municipio elegido" }
        }

        val reporte = Reporte(
            titulo = request.titulo,
            descripcion = request.descripcion,
            direccionReferencia = request.direccionReferencia,
            latitud = request.latitud,
            longitud = request.longitud,
            idCategoria = request.idCategoria,
            idBarrio = request.idBarrio,
            idPrioridad = request.idPrioridad ?: 2,
            idEstado = 1,
            ciudadano = usuario
        )

        val guardado = reporteRepository.save(reporte)

        val historial = HistorialEstado(
            reporte = guardado,
            idEstado = 1,
            funcionario = usuario,
            observacion = "Reporte creado por el ciudadano"
        )
        historialEstadoRepository.save(historial)
        auditoria.registrar(usuario, "REPORTE_CREADO", "REPORTE", guardado.idReporte, "Creó el reporte ${guardado.idReporte}")

        return mapearAResponseObjeto(guardado)
    }

    // LISTADO ULTRA RÁPIDO (< 1 segundo)
    @Transactional(readOnly = true)
    fun listarTodos(): List<ReporteResponse> {
        val resultados = reporteRepository.findAllReportesOptimizado()
        val validaciones = validacionImagenRepository.findAllByEstadoTrue().associateBy { it.evidencia.idEvidencia }
        val evidencias = evidenciaRepository.findAll().groupBy { it.reporte.idReporte }

        return resultados.map { row ->
            val idEstado = (row[8] as Number).toInt()
            val idReporte = (row[0] as Number).toInt()
            val idTecnicoRaw = row[12] as? UUID
            val nombreTecnicoRaw = row[13] as? String
            val tieneTecnico = idTecnicoRaw != null
            val evidenciasDelReporte = evidencias[idReporte].orEmpty().map { evidencia ->
                evidenciaResponse(evidencia, validaciones[evidencia.idEvidencia])
            }

            ReporteResponse(
                idReporte = idReporte,
                titulo = row[1] as String,
                descripcion = row[2] as String,
                direccionReferencia = row[3] as? String,
                latitud = row[4] as BigDecimal,
                longitud = row[5] as BigDecimal,
                fechaReporte = (row[6] as Timestamp).toLocalDateTime(),
                idCategoria = (row[7] as Number).toInt(),
                idEstado = idEstado,
                idPrioridad = (row[9] as Number).toInt(), // Mapea la prioridad real guardada
                idCiudadano = row[10] as? UUID,
                nombreCiudadano = row[11] as? String ?: "Desconocido",
                idTecnico = if (tieneTecnico) idTecnicoRaw else null,
                nombreTecnico = if (tieneTecnico) nombreTecnicoRaw else null,
                evidencias = evidenciasDelReporte,
                idBarrio = (row[14] as? Number)?.toInt(),
                nombreBarrio = row[15] as? String,
                correoTecnico = row[16] as? String
            )
        }
    }

    fun listarMisReportes(correo: String): List<ReporteResponse> {
        val usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow { RuntimeException("Usuario no encontrado") }
        return reporteRepository.findByCiudadanoIdUsuario(usuario.idUsuario!!)
            .map { mapearAResponseObjeto(it) }
    }

    fun listarAsignadosATecnico(correo: String): List<ReporteResponse> {
        val tecnico = usuarioRepository.findByCorreo(correo)
            .orElseThrow { RuntimeException("Técnico no encontrado") }
        return reporteRepository.findReportesByTecnicoId(tecnico.idUsuario!!)
            .map { mapearAResponseObjeto(it, tecnico) }
    }

    // ASIGNACIÓN CON ACTUALIZACIÓN DE PRIORIDAD GARANTIZADA
    @Transactional
    fun asignarTecnico(
        idReporte: Int,
        request: AsignarReporteRequest,
        correoAsistente: String
    ): ReporteResponse {
        val reporte = reporteRepository.findById(idReporte)
            .orElseThrow { RuntimeException("Reporte no encontrado") }

        val tecnico = usuarioRepository.findById(request.idTecnico)
            .orElseThrow { IllegalArgumentException("El técnico seleccionado no existe") }

        val asistente = usuarioRepository.findByCorreo(correoAsistente)
            .orElseThrow { RuntimeException("Asistente no encontrado") }

        require(tecnico.idRol == 4) { "El usuario asignado debe tener el rol Técnico" }
        require(tecnico.estado) { "No se puede asignar un técnico inactivo" }
        require(reporte.idEstado == 1 || reporte.idEstado == 2) { "El reporte no está disponible para asignación" }
        if (request.idPrioridad != null) {
            reporte.idPrioridad = request.idPrioridad
        }
        reporte.idEstado = 2 // Asignado

        val guardado = reporteRepository.saveAndFlush(reporte)

        // Conserva el historial y crea una asignación activa nueva para disparar la notificación SQL.
        asignacionRepository.findByReporte_IdReporteAndEstadoTrue(idReporte).forEach { asignacion ->
            asignacion.estado = false
            asignacionRepository.save(asignacion)
        }
        asignacionRepository.save(Asignacion(reporte = guardado, funcionario = tecnico))

        // Historial
        val historial = HistorialEstado(
            reporte = guardado,
            idEstado = 2,
            funcionario = asistente,
            observacion = "Asignado a: ${tecnico.primerNombre} ${tecnico.primerApellido} con Prioridad: ${guardado.idPrioridad}"
        )
        historialEstadoRepository.save(historial)
        auditoria.registrar(asistente, "REPORTE_ASIGNADO", "REPORTE", guardado.idReporte, "Asignó el reporte a ${tecnico.correo}")

        return mapearAResponseObjeto(guardado, tecnico)
    }

    @Transactional
    fun cambiarEstado(idReporte: Int, request: CambiarEstadoRequest, correoResponsable: String): ReporteResponse {
        val reporte = reporteRepository.findById(idReporte)
            .orElseThrow { RuntimeException("Reporte no encontrado") }
        val responsable = usuarioRepository.findByCorreo(correoResponsable)
            .orElseThrow { RuntimeException("Usuario no encontrado") }
        val nuevoEstadoId = request.idEstado
        require(estadoRepository.existsById(nuevoEstadoId.toLong())) { "Estado no válido" }
        require(nuevoEstadoId in 1..4) { "La transición solicitada no está permitida" }
        if (nuevoEstadoId == 2) {
            require(asignacionRepository.findByReporte_IdReporteAndEstadoTrue(idReporte).isNotEmpty()) {
                "Asigna un técnico antes de pasar el reporte a En Proceso"
            }
        }
        if (nuevoEstadoId == 3 || nuevoEstadoId == 4) {
            require(!request.observacion.isNullOrBlank()) { "Agrega una observación para cerrar o rechazar el reporte" }
        }
        if (responsable.idRol == 4) {
            require(esTecnicoAsignado(reporte.idReporte!!, responsable)) { "El reporte no está asignado a este técnico" }
            require(nuevoEstadoId == 2) { "El técnico solo puede iniciar el trabajo desde esta operación" }
        }
        reporte.idEstado = nuevoEstadoId
        if (nuevoEstadoId == 3 || nuevoEstadoId == 4) reporte.observacionesCierre = request.observacion!!.trim()
        val guardado = reporteRepository.save(reporte)
        historialEstadoRepository.save(HistorialEstado(reporte = guardado, idEstado = nuevoEstadoId, funcionario = responsable,
            observacion = request.observacion?.trim()?.takeIf { it.isNotEmpty() } ?: "Estado actualizado"))
        auditoria.registrar(responsable, "ESTADO_REPORTE_ACTUALIZADO", "REPORTE", guardado.idReporte, "Actualizó el estado a $nuevoEstadoId")
        return mapearAResponseObjeto(guardado)
    }

    @Transactional
    fun resolverReporte(idReporte: Int, request: ResolverReporteRequest, correoTecnico: String): ReporteResponse {
        val reporte = reporteRepository.findById(idReporte)
            .orElseThrow { RuntimeException("Reporte no encontrado") }
        val tecnico = usuarioRepository.findByCorreo(correoTecnico)
            .orElseThrow { RuntimeException("Técnico no encontrado") }
        require(esTecnicoAsignado(idReporte, tecnico)) { "El reporte no está asignado a este técnico" }
        require(reporte.idEstado == 2) { "El reporte debe estar en proceso para resolverse" }
        reporte.idEstado = 3
        reporte.observacionesCierre = request.observacion
        val guardado = reporteRepository.save(reporte)
        comentarioRepository.save(Comentario(comentario = request.observacion.trim(), usuario = tecnico, reporte = guardado))
        historialEstadoRepository.save(HistorialEstado(reporte = guardado, idEstado = 3, funcionario = tecnico, observacion = request.observacion))
        auditoria.registrar(tecnico, "REPORTE_RESUELTO", "REPORTE", guardado.idReporte, "Resolvió el reporte")
        return mapearAResponseObjeto(guardado)
    }

    @Transactional(readOnly = true)
    fun historial(idReporte: Int, correo: String): List<HistorialEstadoResponse> {
        validarAcceso(idReporte, correo)
        return historialEstadoRepository.findByReporteIdReporteOrderByFechaCambioAsc(idReporte).map {
            HistorialEstadoResponse(it.idHistorial, it.idEstado, it.observacion, it.fechaCambio,
                it.funcionario?.let { funcionario -> "${funcionario.primerNombre} ${funcionario.primerApellido}".trim() } ?: "Sistema")
        }
    }

    @Transactional(readOnly = true)
    fun evidencias(idReporte: Int, correo: String): List<EvidenciaResponse> {
        validarAcceso(idReporte, correo)
        return evidenciaRepository.findByReporteIdReporte(idReporte).map { evidencia ->
            evidenciaResponse(evidencia, evidencia.idEvidencia?.let { validacionImagenRepository.findByEvidenciaIdEvidenciaAndEstadoTrue(it) })
        }
    }

    @Transactional
    fun cargarEvidencia(idReporte: Int, archivo: MultipartFile, correo: String): EvidenciaResponse {
        val reporte = reporteRepository.findById(idReporte).orElseThrow { RuntimeException("Reporte no encontrado") }
        val ciudadano = usuarioRepository.findByCorreo(correo).orElseThrow { RuntimeException("Usuario no encontrado") }
        val esPropietario = reporte.ciudadano?.idUsuario == ciudadano.idUsuario
        val esTecnicoAsignado = ciudadano.idRol == 4 && esTecnicoAsignado(idReporte, ciudadano)
        require(esPropietario || esTecnicoAsignado) { "Solo el ciudadano dueño o el técnico asignado pueden adjuntar evidencias" }
        require(!archivo.isEmpty) { "Debe seleccionar un archivo" }
        require(archivo.size <= 5 * 1024 * 1024) { "La evidencia no puede superar 5 MB" }
        val contentType = archivo.contentType?.lowercase()?.substringBefore(';') ?: ""
        require(contentType in setOf("image/jpeg", "image/png")) { "Selecciona una imagen JPG o PNG" }
        val original = Path.of(archivo.originalFilename ?: "evidencia").fileName.toString().take(150)
        val stored = "${UUID.randomUUID()}-${original.replace(Regex("[^A-Za-z0-9._-]"), "_")}"
        val destino = Path.of(uploadDir).toAbsolutePath().normalize()
        val relativePath = "evidencias/$stored"
        val evidenceDirectory = destino.resolve("evidencias").normalize()
        Files.createDirectories(evidenceDirectory)
        archivo.inputStream.use { Files.copy(it, evidenceDirectory.resolve(stored), StandardCopyOption.REPLACE_EXISTING) }
        val tipoArchivo = if (contentType == "image/png") "png" else if (original.substringAfterLast('.', "").equals("jpeg", true)) "jpeg" else "jpg"
        val tamanoMb = BigDecimal.valueOf(archivo.size / (1024.0 * 1024.0))
            .setScale(2, java.math.RoundingMode.HALF_UP).max(BigDecimal("0.01"))
        val evidencia = evidenciaRepository.save(com.ray.myfirtsproject.model.EvidenciaReporte(
            reporte = reporte, nombreOriginal = original, nombreAlmacenado = relativePath,
            tipoContenido = tipoArchivo, tamanioMb = tamanoMb
        ))
        val categoria = categoriaRepository.findById(reporte.idCategoria.toLong()).orElse(null)?.nombre ?: "Sin categoría"
        val validation = visionClient.validate(archivo.bytes, original, contentType, categoria, reporte.descripcion)
        val savedValidation = validacionImagenRepository.save(com.ray.myfirtsproject.model.ValidacionImagen(
            evidencia = evidencia,
            resultado = validation.result,
            requiereRevisionHumana = validation.requiresHumanReview,
            mensaje = validation.message,
            deteccionesJson = validation.detectionsJson,
            modeloDisponible = validation.modelAvailable,
            umbralConfianza = validation.confidenceThreshold
        ))
        auditoria.registrar(ciudadano, "EVIDENCIA_CARGADA", "REPORTE", idReporte, "Adjuntó una evidencia fotográfica")
        return evidenciaResponse(evidencia, savedValidation)
    }

    @Transactional(readOnly = true)
    fun archivoEvidencia(idEvidencia: Int, correo: String): Pair<Path, String> {
        val evidencia = evidenciaRepository.findById(idEvidencia).orElseThrow { RuntimeException("Evidencia no encontrada") }
        validarAcceso(evidencia.reporte.idReporte!!, correo)
        val root = Path.of(uploadDir).toAbsolutePath().normalize()
        val archivo = root.resolve(evidencia.nombreAlmacenado).normalize()
        require(archivo.startsWith(root) && Files.exists(archivo)) { "Archivo no encontrado" }
        val contentType = when (evidencia.tipoContenido.lowercase()) {
            "png" -> "image/png"
            "jpeg" -> "image/jpeg"
            else -> "image/jpeg"
        }
        return archivo to contentType
    }

    @Transactional(readOnly = true)
    fun buscar(categoria: Int?, estado: Int?, fechaDesde: String?, fechaHasta: String?, ubicacion: String?): List<ReporteResponse> {
        val desde = fechaDesde?.let { LocalDate.parse(it).atStartOfDay() }
        val hasta = fechaHasta?.let { LocalDate.parse(it).plusDays(1).atStartOfDay() }
        return reporteRepository.findAll().asSequence()
            .filter { categoria == null || it.idCategoria == categoria }
            .filter { estado == null || it.idEstado == estado }
            .filter { desde == null || !it.fechaReporte.isBefore(desde) }
            .filter { hasta == null || it.fechaReporte.isBefore(hasta) }
            .filter { ubicacion.isNullOrBlank() || it.direccionReferencia.orEmpty().contains(ubicacion, true) }
            .map { mapearAResponseObjeto(it) }.toList()
    }

    @Transactional(readOnly = true)
    fun estadisticas(): EstadisticasResponse {
        val reportes = reporteRepository.findAll()
        val categorias = categoriaRepository.findAll().associate { it.idCategoria!!.toInt() to it.nombre }
        val estados = estadoRepository.findAll().associate { it.idEstado!!.toInt() to it.nombre }
        val prioridades = jdbc.query("select id_prioridad, nombre from public.prioridad where estado = true") { rs, _ -> rs.getInt(1) to rs.getString(2) }.toMap()
        val barrios = barrioRepository.findAll().associate { it.idBarrio to it.nombre }
        val activeCategories = categoriaRepository.findAll().filter { it.estado }.associate { it.idCategoria!!.toInt() to it.nombre }
        val porEstado = reportes.groupingBy { estados[it.idEstado] ?: "Estado ${it.idEstado}" }.eachCount().mapValues { it.value.toLong() }
        val porCategoriaMutable = reportes.groupingBy { categorias[it.idCategoria] ?: "Categoría ${it.idCategoria}" }.eachCount().mapValues { it.value.toLong() }.toMutableMap()
        activeCategories.values.forEach { porCategoriaMutable.putIfAbsent(it, 0L) }
        val porMes = reportes.groupingBy { it.fechaReporte.toLocalDate().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM")) }
            .eachCount().toSortedMap().mapValues { it.value.toLong() }
        val porPrioridad = reportes.groupingBy { prioridades[it.idPrioridad] ?: "Prioridad ${it.idPrioridad}" }
            .eachCount().mapValues { it.value.toLong() }
        val porBarrio = reportes.groupingBy { barrios[it.idBarrio] ?: "Barrio ${it.idBarrio}" }
            .eachCount().mapValues { it.value.toLong() }.toList().sortedByDescending { it.second }.take(10).toMap()
        return EstadisticasResponse(
            totalReportes = reportes.size.toLong(), porEstado = porEstado,
            porCategoria = porCategoriaMutable.toSortedMap(), pendientes = reportes.count { it.idEstado == 1 }.toLong(),
            resueltos = reportes.count { it.idEstado == 3 }.toLong(), totalUsuarios = usuarioRepository.count(),
            usuariosActivos = usuarioRepository.countByEstadoTrue(), porMes = porMes, porPrioridad = porPrioridad,
            porBarrio = porBarrio, evidencias = evidenciaRepository.count(), validacionesImagen = validacionImagenRepository.count()
        )
    }

    private fun validarAcceso(idReporte: Int, correo: String) {
        val reporte = reporteRepository.findById(idReporte).orElseThrow { RuntimeException("Reporte no encontrado") }
        val usuario = usuarioRepository.findByCorreo(correo).orElseThrow { RuntimeException("Usuario no encontrado") }
        if (usuario.idRol == 3) require(reporte.ciudadano?.idUsuario == usuario.idUsuario) { "Sin acceso a este reporte" }
        if (usuario.idRol == 4) require(esTecnicoAsignado(idReporte, usuario)) { "Sin acceso a este reporte" }
    }

    private fun esTecnicoAsignado(idReporte: Int, tecnico: Usuario): Boolean =
        asignacionRepository.existsByReporte_IdReporteAndFuncionario_IdUsuarioAndEstadoTrue(idReporte, tecnico.idUsuario!!)

    private fun evidenciaResponse(
        e: com.ray.myfirtsproject.model.EvidenciaReporte,
        validation: com.ray.myfirtsproject.model.ValidacionImagen? = null
    ) = EvidenciaResponse(
        e.idEvidencia, e.nombreOriginal, e.tipoContenido,
        e.tamanioMb.multiply(BigDecimal.valueOf(1024L * 1024L)).toLong(), e.fechaCarga,
        "/reportes/evidencias/${e.idEvidencia}/archivo",
        validation?.let { ValidacionImagenResponse(it.resultado, it.requiereRevisionHumana, it.mensaje, it.deteccionesJson, it.modeloDisponible, it.umbralConfianza) }
    )

    private fun mapearAResponseObjeto(reporte: Reporte, tecnico: com.ray.myfirtsproject.model.Usuario? = null): ReporteResponse {
        val tecnicoAsignado = tecnico ?: reporte.idReporte?.let { id ->
            asignacionRepository.findByReporte_IdReporteAndEstadoTrue(id).firstOrNull()?.funcionario
        }
        return ReporteResponse(
            idReporte = reporte.idReporte,
            titulo = reporte.titulo,
            descripcion = reporte.descripcion,
            direccionReferencia = reporte.direccionReferencia,
            latitud = reporte.latitud,
            longitud = reporte.longitud,
            fechaReporte = reporte.fechaReporte,
            idCategoria = reporte.idCategoria,
            idEstado = reporte.idEstado,
            idPrioridad = reporte.idPrioridad,
            idCiudadano = reporte.ciudadano?.idUsuario,
            nombreCiudadano = "${reporte.ciudadano?.primerNombre ?: ""} ${reporte.ciudadano?.primerApellido ?: ""}".trim(),
            idTecnico = tecnicoAsignado?.idUsuario,
            nombreTecnico = tecnicoAsignado?.let { "${it.primerNombre} ${it.primerApellido}".trim() },
            evidencias = reporte.idReporte?.let { id ->
                evidenciaRepository.findByReporteIdReporte(id).map { evidencia ->
                    evidenciaResponse(evidencia, evidencia.idEvidencia?.let { validacionImagenRepository.findByEvidenciaIdEvidenciaAndEstadoTrue(it) })
                }
            } ?: emptyList(),
            idBarrio = reporte.idBarrio,
            nombreBarrio = barrioRepository.findById(reporte.idBarrio).orElse(null)?.nombre,
            correoTecnico = tecnicoAsignado?.correo
        )
    }
}
