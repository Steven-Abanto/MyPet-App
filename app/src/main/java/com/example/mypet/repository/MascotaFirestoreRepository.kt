package com.example.mypet.repository

import android.content.Context
import com.example.mypet.dao.MascotaDAO
import com.example.mypet.entity.Mascota
import com.example.mypet.entity.firestore.MascotaFirestore
import com.example.mypet.firebase.FirestoreHelper

class MascotaFirestoreRepository(private val context: Context) {

    private val mascotasRef = FirestoreHelper.db.collection("mascotas")
    private val mascotaDAO = MascotaDAO(context)

    fun registrarMascota(
        idUsuarioLocal: Int,
        mascotaFirestore: MascotaFirestore,
        mascotaLocalBase: Mascota,
        onResult: (Boolean, String?) -> Unit
    ) {
        val newDoc = mascotasRef.document()

        newDoc.set(mascotaFirestore)
            .addOnSuccessListener {
                val mascotaLocal = mascotaLocalBase.copy(
                    firestoreId = newDoc.id,
                    idUsuario = idUsuarioLocal
                )

                val resultadoLocal = mascotaDAO.insert(mascotaLocal)

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

    fun sincronizarMascotasDeUsuarioALocal(
        firebaseUid: String,
        idUsuarioLocal: Int,
        onResult: (Boolean, String?) -> Unit
    ) {
        mascotasRef
            .whereEqualTo("firebaseUid", firebaseUid)
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    mascotaDAO.eliminarPorIdUsuario(idUsuarioLocal)

                    for (doc in snapshot.documents) {
                        val mascotaFirestore = doc.toObject(MascotaFirestore::class.java)

                        if (mascotaFirestore != null) {
                            val mascotaLocal = Mascota(
                                idMascota = 0,
                                firestoreId = doc.id,
                                idUsuario = idUsuarioLocal,
                                nombres = mascotaFirestore.nombres,
                                fechaNacimiento = mascotaFirestore.fechaNacimiento,
                                idEspecie = mascotaFirestore.idEspecie,
                                idRaza = mascotaFirestore.idRaza,
                                sexo = mascotaFirestore.sexo,
                                pesoActual = mascotaFirestore.pesoActual,
                                esEsterilizado = mascotaFirestore.esEsterilizado,
                                tieneChip = mascotaFirestore.tieneChip,
                                notas = mascotaFirestore.notas,
                                activo = mascotaFirestore.activo
                            )

                            mascotaDAO.guardarOActualizar(mascotaLocal)
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