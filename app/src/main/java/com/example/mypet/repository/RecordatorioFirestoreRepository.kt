package com.example.mypet.repository

import android.content.Context
import com.example.mypet.dao.CategoriaRecordatorioDAO
import com.example.mypet.dao.MascotaDAO
import com.example.mypet.dao.RecordatorioDAO
import com.example.mypet.dao.TipoRecordatorioDAO
import com.example.mypet.entity.Recordatorio
import com.example.mypet.entity.firestore.RecordatorioFirestore
import com.example.mypet.entity.mappers.RecordatorioMapper
import com.example.mypet.firebase.FirestoreHelper

class RecordatorioFirestoreRepository(private val context: Context) {

    private val recordatoriosRef = FirestoreHelper.db.collection("recordatorios")
    private val recordatorioDAO = RecordatorioDAO(context)
    private val mascotaDAO = MascotaDAO(context)
    private val tipoDAO = TipoRecordatorioDAO(context)
    private val categoriaDAO = CategoriaRecordatorioDAO(context)

    fun registrarRecordatorio(
        recordatorioFirestore: RecordatorioFirestore,
        recordatorioLocalBase: Recordatorio,
        onResult: (Boolean, String?) -> Unit
    ) {
        val newDoc = recordatoriosRef.document()

        newDoc.set(recordatorioFirestore)
            .addOnSuccessListener {
                val recordatorioLocal = recordatorioLocalBase.copy(firestoreId = newDoc.id)
                val resultadoLocal = recordatorioDAO.insert(recordatorioLocal)

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

    fun actualizarRecordatorio(
        firestoreId: String,
        recordatorioFirestore: RecordatorioFirestore,
        recordatorioLocal: Recordatorio,
        onResult: (Boolean, String?) -> Unit
    ) {
        recordatoriosRef.document(firestoreId)
            .set(recordatorioFirestore)
            .addOnSuccessListener {
                val resultadoLocal = recordatorioDAO.guardarOActualizar(recordatorioLocal)
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

    fun sincronizarRecordatoriosDeUsuarioALocal(
        firebaseUid: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        recordatoriosRef
            .whereEqualTo("uidUsuario", firebaseUid)
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    for (doc in snapshot.documents) {
                        val recordatorioFirestore = doc.toObject(RecordatorioFirestore::class.java)

                        if (recordatorioFirestore != null) {
                            val mascotaLocal = mascotaDAO.obtenerPorFirestoreId(recordatorioFirestore.mascotaFirestoreId)
                                ?: continue

                            val categoria = categoriaDAO.obtenerPorNombre(recordatorioFirestore.categoriaNombre)
                                ?: continue

                            val tipo = tipoDAO.obtenerPorNombreYCategoria(
                                recordatorioFirestore.tipoNombre,
                                categoria.idCategoriaRecordatorio
                            ) ?: continue

                            val recordatorioLocal = RecordatorioMapper.toLocal(
                                firestoreId = doc.id,
                                idMascotaLocal = mascotaLocal.idMascota,
                                idTipoRecordatorioLocal = tipo.idTipoRecordatorio,
                                recordatorioFirestore = recordatorioFirestore
                            )

                            recordatorioDAO.guardarOActualizar(recordatorioLocal)
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