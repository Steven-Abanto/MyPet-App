package com.example.mypet.entity

data class Usuario(
    val idUsuario: Int = 0,
    val firebaseUid: String,
    val nombres: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val email: String,
    val telefono: String,
    val fechaNacimiento: String,
    val pronombre: String,
//    val contrasenaHashed: String,
//    val fechaCreacion: String,
    val activo: Boolean
)
