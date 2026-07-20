package com.example.dsm_investigacion01

import java.util.UUID

data class Session(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(), // Fecha y hora en que se completó
    val durationMinutes: Int = 25 // Duración del enfoque
)