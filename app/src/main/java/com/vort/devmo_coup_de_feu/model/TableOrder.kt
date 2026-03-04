package com.vort.devmo_coup_de_feu.model

enum class TableStatus { ACTIVE, WARNING, CRITICAL }

data class StationProgress(
    val ready: Int,
    val total: Int
)

data class Stations(
    val froid: StationProgress,
    val poisson: StationProgress,
    val viande: StationProgress
)

data class TableOrder(
    val id: Int,
    val table: Int,
    val covers: Int,
    val time: String,
    val stations: Stations,
    val status: TableStatus
)
