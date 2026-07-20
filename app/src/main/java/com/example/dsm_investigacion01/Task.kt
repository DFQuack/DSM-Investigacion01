package com.example.dsm_investigacion01

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    var completed: Boolean = false
)