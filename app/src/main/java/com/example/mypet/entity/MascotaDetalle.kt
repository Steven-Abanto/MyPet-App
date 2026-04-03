package com.example.mypet.entity

data class MascotaDetalle(
    val idMascota: Int,
    val firestoreId: String?,
    val idUsuario: Int,
    val nombres: String,
    val fechaNacimiento: String,
    val idEspecie: Int,
    val nombreEspecie: String,
    val idRaza: Int,
    val nombreRaza: String,
    val sexo: String,
    val pesoActual: String,
    val esEsterilizado: Boolean,
    val tieneChip: Boolean,
    val notas: String,
    val activo: Boolean
)