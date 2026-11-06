package com.ray.myfirtsproject.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Service
class VisionClient(
    @param:Value("\${app.vision.base-url}") private val baseUrl: String,
    @param:Value("\${app.vision.enabled:false}") private val enabled: Boolean
) {
    private val client = RestClient.create()

    fun validate(image: ByteArray, filename: String, contentType: String, category: String, description: String): VisionResult {
        if (!enabled) return VisionResult.uncertain("La validación visual está deshabilitada; requiere revisión humana.")
        return runCatching {
            val body = LinkedMultiValueMap<String, Any>()
            body.add("category", category)
            body.add("description", description)
            body.add("image", object : ByteArrayResource(image) {
                override fun getFilename(): String = filename
            })
            val result = client.post().uri("$baseUrl/v1/vision/validate")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .body(body).retrieve().body(Map::class.java) as? Map<*, *>
                ?: error("Respuesta vacía de la API visual")
            VisionResult(
                result = result["result"]?.toString() ?: "uncertain",
                requiresHumanReview = result["requires_human_review"] as? Boolean ?: true,
                message = result["message"]?.toString() ?: "La validación requiere revisión humana.",
                detectionsJson = result["detections"]?.let { com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().writeValueAsString(it) } ?: "[]",
                modelAvailable = result["model_available"] as? Boolean ?: false,
                confidenceThreshold = (result["confidence_threshold"] as? Number)?.toDouble() ?: 0.35
            )
        }.getOrElse { VisionResult.uncertain("La API visual no está disponible; el reporte queda para revisión humana.") }
    }
}

data class VisionResult(
    val result: String,
    val requiresHumanReview: Boolean,
    val message: String,
    val detectionsJson: String,
    val modelAvailable: Boolean,
    val confidenceThreshold: Double
) {
    companion object {
        fun uncertain(message: String) = VisionResult("uncertain", true, message, "[]", false, 0.35)
    }
}
