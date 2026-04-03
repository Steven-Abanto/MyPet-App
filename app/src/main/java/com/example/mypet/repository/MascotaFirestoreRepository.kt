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
                val mascotaLocal = mascotaLocalBase.copy(firestoreId = newDoc.id)
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
}