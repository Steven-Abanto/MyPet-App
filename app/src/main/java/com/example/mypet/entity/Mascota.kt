package com.example.mypet.entity

data class Mascota(
    var idMascota: Int,
    var firestoreId: String?,
    var idUsuario: Int,
    var nombres: String,
    var fechaNacimiento: String,
    var idEspecie: Int,
    var idRaza: Int,
    var sexo: String,
    var pesoActual: String,
    var esEsterilizado: Boolean,
    var tieneChip: Boolean,
    var notas: String,
    var activo: Boolean
)