package com.vort.devmo_coup_de_feu.model

enum class Priority { NORMAL, URGENT }

data class Ticket(
    val id: Int,
    val table: Int,
    val items: List<String>,
    val time: String,
    val priority: Priority
)
