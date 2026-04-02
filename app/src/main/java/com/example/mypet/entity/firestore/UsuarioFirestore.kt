package com.example.mypet.entity.firestore

data class UsuarioFirestore(
    val uid: String = "",
    val nombres: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val email: String = "",
    val telefono: String = "",
    val fechaNacimiento: String = "",
    val pronombre: String = "",
    val activo: Boolean = true
)