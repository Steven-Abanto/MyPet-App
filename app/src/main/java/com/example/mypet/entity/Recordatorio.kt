package com.example.mypet.entity

data class Recordatorio(
    val idRecordatorio: Int,
    val idUsuario: Int,
    val idMascota: Int,
    val titulo: String,
    val descripcion: String,
    val idTipoRecordatorio: Int,
    val costo: Double,
    val fechaInicio: String,
    val fechaFin: String,
    val seRepite: Boolean,
    val frecuencia: String,
    val fechaCreacion: String,
    val activo: Boolean
)
