package com.example.mypet.entity.mappers

data class RecordatorioDetalle(
    val idRecordatorio: Int,
    val firestoreId: String?,
    val idMascota: Int,
    val nombreMascota: String,
    val titulo: String,
    val descripcion: String?,
    val idTipoRecordatorio: Int,
    val nombreTipoRecordatorio: String,
    val fechaInicio: String,
    val fechaFin: String?,
    val frecuencia: String?,
    val activo: Boolean
)