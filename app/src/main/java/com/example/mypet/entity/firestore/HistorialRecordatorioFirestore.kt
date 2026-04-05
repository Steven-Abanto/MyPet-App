package com.example.mypet.entity.firestore

data class HistorialRecordatorioFirestore(
    val uidUsuario: String = "",
    val recordatorioFirestoreId: String = "",
    val fechaProgramada: String = "",
    val fechaCompletado: String? = null,
    val notas: String? = null,
    val estado: String = "",
    val fechaCreacion: String? = null,
    val ultimaModificacion: String? = null,
    val activo: Boolean = true
)