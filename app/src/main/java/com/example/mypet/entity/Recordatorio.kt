package com.example.mypet.entity

data class Recordatorio(
    val idRecordatorio: Int = 0,
    val firestoreId: String? = null,
    val idMascota: Int,
    val titulo: String,
    val descripcion: String? = null,
    val idTipoRecordatorio: Int,
    val fechaInicio: String,
    val fechaFin: String? = null,
    val frecuencia: String? = null, // UNA_VEZ, DIARIO, SEMANAL, MENSUAL, ANUAL
    val fechaCreacion: String? = null,
    val ultimaModificacion: String? = null,
    val activo: Boolean = true
)