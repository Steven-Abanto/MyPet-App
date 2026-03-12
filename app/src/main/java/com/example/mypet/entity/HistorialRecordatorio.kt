package com.example.mypet.entity

data class HistorialRecordatorio(
    val idHistorial: Int,
    val idRecordatorio: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val fechaCompletado: String,
    val notas: String,
    val estado: String
)
