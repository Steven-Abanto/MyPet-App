package com.example.mypet.entity.firestore

data class MascotaFirestore(
    val firebaseUid: String = "",
    val nombres: String = "",
    val fechaNacimiento: String = "",
    val idEspecie: Int = 0,
    val idRaza: Int = 0,
    val sexo: String = "",
    val pesoActual: String = "",
    val esEsterilizado: Boolean = false,
    val tieneChip: Boolean = false,
    val notas: String = "",
    val activo: Boolean = true
)