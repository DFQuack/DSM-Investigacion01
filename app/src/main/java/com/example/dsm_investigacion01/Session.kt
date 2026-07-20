package com.example.dsm_investigacion01

import java.util.UUID

data class Session (
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 25,
    val taskName: String = "Sesión Libre"
)
