package com.ray.myfirtsproject.service

import com.ray.myfirtsproject.dto.*
import com.ray.myfirtsproject.model.Asignacion
import com.ray.myfirtsproject.model.HistorialEstado
import com.ray.myfirtsproject.model.Reporte
import com.ray.myfirtsproject.model.Usuario
import com.ray.myfirtsproject.repository.AsignacionRepository
import com.ray.myfirtsproject.repository.EvidenciaReporteRepository
import com.ray.myfirtsproject.repository.HistorialEstadoRepository
import com.ray.myfirtsproject.repository.ReporteRepository
import com.ray.myfirtsproject.repository.UsuarioRepository
import com.ray.myfirtsproject.repository.CategoriaRepository
import com.ray.myfirtsproject.repository.EstadoReporteRepository
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

@Service
class ReporteService(
    private val reporteRepository: ReporteRepository,
    private val usuarioRepository: UsuarioRepository,
    private val asignacionRepository: AsignacionRepository,
    private val historialEstadoRepository: HistorialEstadoRepository,
    private val evidenciaRepository: EvidenciaReporteRepository,
    private val categoriaRepository: CategoriaRepository,
    private val estadoRepository: EstadoReporteRepository,
    private val auditoria: AuditoriaService,
    @Value("\${app.upload-dir:uploads}") private val uploadDir: String
) {

    @Transactional
    fun crear(request: CrearReporteRequest, correo: String): ReporteResponse {
        val usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow { RuntimeException("Usuario no encontrado") }

        val reporte = Reporte(
            titulo = request.titulo,
            descripcion = request.descripcion,
            direccionReferencia = request.direccionReferencia,
            latitud = request.latitud,
            longitud = request.longitud,
            idCategoria = request.idCategoria,
            idBarrio = request.idBarrio ?: 1,
            idPrioridad = request.idPrioridad ?: 1,
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

        return resultados.map { row ->
            val idEstado = (row[8] as Number).toInt()
            val idTecnicoRaw = row[12] as? UUID
            val nombreTecnicoRaw = row[13] as? String

            // Solo enviar el técnico si el reporte NO está en Pendiente (idEstado != 1)
            val tieneTecnico = idEstado != 1

            ReporteResponse(
                idReporte = (row[0] as Number).toInt(),
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
                nombreTecnico = if (tieneTecnico) nombreTecnicoRaw else null
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
            .map { mapearAResponseObjeto(it) }
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
            .orElseThrow { RuntimeException("Técnico no encontrado") }

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

        // Registrar o actualizar la asignación
        val asignacionExistente = asignacionRepository.findAll()
            .find { it.reporte?.idReporte == idReporte }

        if (asignacionExistente != null) {
            asignacionExistente.funcionario = tecnico
            asignacionRepository.save(asignacionExistente)
        } else {
            val nuevaAsignacion = Asignacion(reporte = guardado, funcionario = tecnico)
            asignacionRepository.save(nuevaAsignacion)
        }

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
    fun cambiarEstado(idReporte: Int, nuevoEstadoId: Int, correoResponsable: String): ReporteResponse {
        val reporte = reporteRepository.findById(idReporte)
            .orElseThrow { RuntimeException("Reporte no encontrado") }
        val responsable = usuarioRepository.findByCorreo(correoResponsable)
            .orElseThrow { RuntimeException("Usuario no encontrado") }
        require(estadoRepository.existsById(nuevoEstadoId.toLong())) { "Estado no válido" }
        if (responsable.idRol == 4) {
            require(esTecnicoAsignado(reporte.idReporte!!, responsable)) { "El reporte no está asignado a este técnico" }
            require(nuevoEstadoId == 3) { "El técnico solo puede iniciar el trabajo desde esta operación" }
        }
        reporte.idEstado = nuevoEstadoId
        val guardado = reporteRepository.save(reporte)
        historialEstadoRepository.save(HistorialEstado(reporte = guardado, idEstado = nuevoEstadoId, funcionario = responsable,
            observacion = "Estado actualizado"))
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
        require(reporte.idEstado == 3) { "El reporte debe estar en proceso para resolverse" }
        reporte.idEstado = 4
        reporte.observacionesCierre = request.observacion
        val guardado = reporteRepository.save(reporte)
        historialEstadoRepository.save(HistorialEstado(reporte = guardado, idEstado = 4, funcionario = tecnico, observacion = request.observacion))
        auditoria.registrar(tecnico, "REPORTE_RESUELTO", "REPORTE", guardado.idReporte, "Resolvió el reporte")
        return mapearAResponseObjeto(guardado)
    }

    @Transactional(readOnly = true)
    fun historial(idReporte: Int, correo: String): List<HistorialEstadoResponse> {
        validarAcceso(idReporte, correo)
        return historialEstadoRepository.findByReporteIdReporteOrderByFechaCambioAsc(idReporte).map {
            HistorialEstadoResponse(it.idHistorial, it.idEstado, it.observacion, it.fechaCambio,
                "${it.funcionario.primerNombre} ${it.funcionario.primerApellido}".trim())
        }
    }

    @Transactional(readOnly = true)
    fun evidencias(idReporte: Int, correo: String): List<EvidenciaResponse> {
        validarAcceso(idReporte, correo)
        return evidenciaRepository.findByReporteIdReporte(idReporte).map { evidenciaResponse(it) }
    }

    @Transactional
    fun cargarEvidencia(idReporte: Int, archivo: MultipartFile, correo: String): EvidenciaResponse {
        val reporte = reporteRepository.findById(idReporte).orElseThrow { RuntimeException("Reporte no encontrado") }
        val ciudadano = usuarioRepository.findByCorreo(correo).orElseThrow { RuntimeException("Usuario no encontrado") }
        require(reporte.ciudadano?.idUsuario == ciudadano.idUsuario) { "Solo el ciudadano dueño puede adjuntar evidencias" }
        require(!archivo.isEmpty) { "Debe seleccionar un archivo" }
        require(archivo.size <= 5 * 1024 * 1024) { "La evidencia no puede superar 5 MB" }
        val contentType = archivo.contentType ?: "application/octet-stream"
        require(contentType.startsWith("image/")) { "Solo se permiten imágenes" }
        val original = Path.of(archivo.originalFilename ?: "evidencia").fileName.toString()
        val stored = "${UUID.randomUUID()}-${original.replace(Regex("[^A-Za-z0-9._-]"), "_")}"
        val destino = Path.of(uploadDir).toAbsolutePath().normalize()
        Files.createDirectories(destino)
        archivo.inputStream.use { Files.copy(it, destino.resolve(stored), StandardCopyOption.REPLACE_EXISTING) }
        val evidencia = evidenciaRepository.save(com.ray.myfirtsproject.model.EvidenciaReporte(
            reporte = reporte, nombreOriginal = original, nombreAlmacenado = stored, tipoContenido = contentType, tamanio = archivo.size
        ))
        auditoria.registrar(ciudadano, "EVIDENCIA_CARGADA", "REPORTE", idReporte, "Adjuntó una evidencia fotográfica")
        return evidenciaResponse(evidencia)
    }

    @Transactional(readOnly = true)
    fun archivoEvidencia(idEvidencia: Long, correo: String): Pair<Path, String> {
        val evidencia = evidenciaRepository.findById(idEvidencia).orElseThrow { RuntimeException("Evidencia no encontrada") }
        validarAcceso(evidencia.reporte.idReporte!!, correo)
        val archivo = Path.of(uploadDir).toAbsolutePath().normalize().resolve(evidencia.nombreAlmacenado).normalize()
        require(archivo.startsWith(Path.of(uploadDir).toAbsolutePath().normalize()) && Files.exists(archivo)) { "Archivo no encontrado" }
        return archivo to evidencia.tipoContenido
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
        return EstadisticasResponse(reportes.size.toLong(),
            reportes.groupingBy { estados[it.idEstado] ?: "Estado ${it.idEstado}" }.eachCount().mapValues { it.value.toLong() },
            reportes.groupingBy { categorias[it.idCategoria] ?: "Categoría ${it.idCategoria}" }.eachCount().mapValues { it.value.toLong() },
            reportes.count { it.idEstado == 1 }.toLong(), reportes.count { it.idEstado == 4 }.toLong())
    }

    private fun validarAcceso(idReporte: Int, correo: String) {
        val reporte = reporteRepository.findById(idReporte).orElseThrow { RuntimeException("Reporte no encontrado") }
        val usuario = usuarioRepository.findByCorreo(correo).orElseThrow { RuntimeException("Usuario no encontrado") }
        if (usuario.idRol == 3) require(reporte.ciudadano?.idUsuario == usuario.idUsuario) { "Sin acceso a este reporte" }
        if (usuario.idRol == 4) require(esTecnicoAsignado(idReporte, usuario)) { "Sin acceso a este reporte" }
    }

    private fun esTecnicoAsignado(idReporte: Int, tecnico: Usuario): Boolean =
        asignacionRepository.findAll().any { it.reporte.idReporte == idReporte && it.funcionario.idUsuario == tecnico.idUsuario }

    private fun evidenciaResponse(e: com.ray.myfirtsproject.model.EvidenciaReporte) = EvidenciaResponse(
        e.idEvidencia, e.nombreOriginal, e.tipoContenido, e.tamanio, e.fechaCarga, "/reportes/evidencias/${e.idEvidencia}/archivo"
    )

    private fun mapearAResponseObjeto(reporte: Reporte, tecnico: com.ray.myfirtsproject.model.Usuario? = null): ReporteResponse {
        val esPendiente = reporte.idEstado == 1
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
            idTecnico = if (!esPendiente) tecnico?.idUsuario else null,
            nombreTecnico = if (!esPendiente && tecnico != null) "${tecnico.primerNombre} ${tecnico.primerApellido}".trim() else null,
            evidencias = reporte.idReporte?.let { evidenciaRepository.findByReporteIdReporte(it).map(::evidenciaResponse) } ?: emptyList()
        )
    }
}
