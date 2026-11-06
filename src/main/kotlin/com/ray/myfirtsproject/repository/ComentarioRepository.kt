package com.ray.myfirtsproject.repository

import com.ray.myfirtsproject.model.Comentario
import org.springframework.data.jpa.repository.JpaRepository

interface ComentarioRepository : JpaRepository<Comentario, Int>
