package com.ray.myfirtsproject.dto.auth

data class TokenResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
)
