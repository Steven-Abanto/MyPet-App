package com.example.mypet.entity.mappers

data class HistorialRecordatorioDetalle(
    val idHistorial: Int,
    val firestoreId: String?,
    val idRecordatorio: Int,
    val tituloRecordatorio: String,
    val nombreMascota: String,
    val fechaProgramada: String,
    val fechaCompletado: String?,
    val notas: String?,
    val estado: String,
    val fechaCreacion: String?,
    val ultimaModificacion: String?,
    val activo: Boolean
)