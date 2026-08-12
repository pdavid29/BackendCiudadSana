package com.ray.myfirtsproject.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.http.HttpStatus

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException, request: HttpServletRequest): ResponseEntity<ApiError> =
        ResponseEntity.badRequest().body(ApiError(status = 400, error = "Bad Request", message = ex.message ?: "Solicitud inválida", path = request.requestURI))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val message = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.badRequest().body(ApiError(status = 400, error = "Bad Request", message = message, path = request.requestURI))
    }

    @ExceptionHandler(ApiException::class)
    fun handleApiException(
        ex: ApiException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val errorResponse = ApiError(
            status = ex.status.value(),
            error = ex.status.reasonPhrase,
            message = ex.message ?: "Credenciales inválidas",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, ex.status)
    }
}
