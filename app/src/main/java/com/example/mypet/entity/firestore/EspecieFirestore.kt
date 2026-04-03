package com.example.mypet.entity.firestore

data class EspecieFirestore(
    val idEspecie: Int,
    val nombreEspecie: String = "",
    val activo: Boolean = true,
    val ordenVisual: Int = 0
)