package com.example.mypet.entity.mappers

import com.example.mypet.entity.HistorialRecordatorio
import com.example.mypet.entity.firestore.HistorialRecordatorioFirestore

object HistorialRecordatorioMapper {

    fun toLocal(
        firestoreId: String,
        idRecordatorioLocal: Int,
        historialFirestore: HistorialRecordatorioFirestore
    ): HistorialRecordatorio {
        return HistorialRecordatorio(
            idHistorial = 0,
            firestoreId = firestoreId,
            idRecordatorio = idRecordatorioLocal,
            fechaProgramada = historialFirestore.fechaProgramada,
            fechaCompletado = historialFirestore.fechaCompletado,
            notas = historialFirestore.notas,
            estado = historialFirestore.estado,
            fechaCreacion = historialFirestore.fechaCreacion,
            ultimaModificacion = historialFirestore.ultimaModificacion,
            activo = historialFirestore.activo
        )
    }

    fun toFirestore(
        uidUsuario: String,
        recordatorioFirestoreId: String,
        historial: HistorialRecordatorio
    ): HistorialRecordatorioFirestore {
        return HistorialRecordatorioFirestore(
            uidUsuario = uidUsuario,
            recordatorioFirestoreId = recordatorioFirestoreId,
            fechaProgramada = historial.fechaProgramada,
            fechaCompletado = historial.fechaCompletado,
            notas = historial.notas,
            estado = historial.estado,
            fechaCreacion = historial.fechaCreacion,
            ultimaModificacion = historial.ultimaModificacion,
            activo = historial.activo
        )
    }
}