package com.example.mypet.entity

data class HistorialRecordatorio(
    val idHistorial: Int = 0,
    val firestoreId: String? = null,
    val idRecordatorio: Int,
    val fechaProgramada: String,
    val fechaCompletado: String? = null,
    val notas: String? = null,
    val estado: String, // PENDIENTE, COMPLETADO, OMITIDO, VENCIDO, CANCELADO
    val fechaCreacion: String? = null,
    val ultimaModificacion: String? = null,
    val activo: Boolean = true
)