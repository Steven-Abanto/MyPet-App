package com.example.mypet.entity.firestore

data class RazaFirestore(
    val idRaza: Int,
    val idEspecie: Int,
    val nombreRaza: String = "",
    val activo: Boolean = true,
    val ordenVisual: Int = 0
)