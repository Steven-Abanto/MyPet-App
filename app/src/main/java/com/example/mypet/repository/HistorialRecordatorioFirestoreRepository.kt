package com.example.mypet.repository

import android.content.Context
import com.example.mypet.dao.HistorialRecordatorioDAO
import com.example.mypet.dao.RecordatorioDAO
import com.example.mypet.entity.HistorialRecordatorio
import com.example.mypet.entity.firestore.HistorialRecordatorioFirestore
import com.example.mypet.entity.mappers.HistorialRecordatorioMapper
import com.example.mypet.firebase.FirestoreHelper

class HistorialRecordatorioFirestoreRepository(private val context: Context) {

    private val historialRef = FirestoreHelper.db.collection("historial_recordatorios")
    private val historialDAO = HistorialRecordatorioDAO(context)
    private val recordatorioDAO = RecordatorioDAO(context)

    fun registrarHistorial(
        historialFirestore: HistorialRecordatorioFirestore,
        historialLocalBase: HistorialRecordatorio,
        onResult: (Boolean, String?) -> Unit
    ) {
        val newDoc = historialRef.document()

        newDoc.set(historialFirestore)
            .addOnSuccessListener {
                val historialLocal = historialLocalBase.copy(firestoreId = newDoc.id)
                val resultadoLocal = historialDAO.insert(historialLocal)

                if (resultadoLocal > 0) {
                    onResult(true, null)
                } else {
                    onResult(false, "Se guardó en Firestore, pero falló el guardado local")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun actualizarHistorial(
        firestoreId: String,
        historialFirestore: HistorialRecordatorioFirestore,
        historialLocal: HistorialRecordatorio,
        onResult: (Boolean, String?) -> Unit
    ) {
        historialRef.document(firestoreId)
            .set(historialFirestore)
            .addOnSuccessListener {
                val resultadoLocal = historialDAO.guardarOActualizar(historialLocal)
                if (resultadoLocal > 0) {
                    onResult(true, null)
                } else {
                    onResult(false, "Se actualizó en Firestore, pero falló la actualización local")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun sincronizarHistorialDeUsuarioALocal(
        firebaseUid: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        historialRef
            .whereEqualTo("uidUsuario", firebaseUid)
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    for (doc in snapshot.documents) {
                        val historialFirestore = doc.toObject(HistorialRecordatorioFirestore::class.java)

                        if (historialFirestore != null) {
                            val recordatorioLocal = recordatorioDAO.obtenerPorFirestoreId(
                                historialFirestore.recordatorioFirestoreId
                            ) ?: continue

                            val historialLocal = HistorialRecordatorioMapper.toLocal(
                                firestoreId = doc.id,
                                idRecordatorioLocal = recordatorioLocal.idRecordatorio,
                                historialFirestore = historialFirestore
                            )

                            historialDAO.guardarOActualizar(historialLocal)
                        }
                    }

                    onResult(true, null)
                } catch (e: Exception) {
                    onResult(false, e.message)
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }
}