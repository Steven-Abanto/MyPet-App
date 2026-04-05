package com.example.mypet.entity.mappers

import com.example.mypet.entity.Recordatorio
import com.example.mypet.entity.firestore.RecordatorioFirestore

object RecordatorioMapper {

    fun toLocal(
        firestoreId: String,
        idMascotaLocal: Int,
        idTipoRecordatorioLocal: Int,
        recordatorioFirestore: RecordatorioFirestore
    ): Recordatorio {
        return Recordatorio(
            idRecordatorio = 0,
            firestoreId = firestoreId,
            idMascota = idMascotaLocal,
            titulo = recordatorioFirestore.titulo,
            descripcion = recordatorioFirestore.descripcion,
            idTipoRecordatorio = idTipoRecordatorioLocal,
            fechaInicio = recordatorioFirestore.fechaInicio,
            fechaFin = recordatorioFirestore.fechaFin,
            frecuencia = recordatorioFirestore.frecuencia,
            fechaCreacion = recordatorioFirestore.fechaCreacion,
            ultimaModificacion = recordatorioFirestore.ultimaModificacion,
            activo = recordatorioFirestore.activo
        )
    }

    fun toFirestore(
        uidUsuario: String,
        mascotaFirestoreId: String,
        categoriaNombre: String,
        tipoNombre: String,
        recordatorio: Recordatorio
    ): RecordatorioFirestore {
        return RecordatorioFirestore(
            uidUsuario = uidUsuario,
            mascotaFirestoreId = mascotaFirestoreId,
            titulo = recordatorio.titulo,
            descripcion = recordatorio.descripcion,
            categoriaNombre = categoriaNombre,
            tipoNombre = tipoNombre,
            fechaInicio = recordatorio.fechaInicio,
            fechaFin = recordatorio.fechaFin,
            frecuencia = recordatorio.frecuencia,
            activo = recordatorio.activo,
            fechaCreacion = recordatorio.fechaCreacion,
            ultimaModificacion = recordatorio.ultimaModificacion
        )
    }
}