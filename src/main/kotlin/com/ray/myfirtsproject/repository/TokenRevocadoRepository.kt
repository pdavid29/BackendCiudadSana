package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.TokenRevocado
import org.springframework.data.jpa.repository.JpaRepository

interface TokenRevocadoRepository : JpaRepository<TokenRevocado, Long> {
    fun existsByToken(token: String): Boolean
}
