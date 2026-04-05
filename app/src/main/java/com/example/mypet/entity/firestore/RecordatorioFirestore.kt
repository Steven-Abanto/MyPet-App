package com.example.mypet.entity.firestore

data class RecordatorioFirestore(
    val uidUsuario: String = "",
    val mascotaFirestoreId: String = "",
    val titulo: String = "",
    val descripcion: String? = null,
    val categoriaNombre: String = "",
    val tipoNombre: String = "",
    val fechaInicio: String = "",
    val fechaFin: String? = null,
    val frecuencia: String? = null,
    val activo: Boolean = true,
    val fechaCreacion: String? = null,
    val ultimaModificacion: String? = null
)