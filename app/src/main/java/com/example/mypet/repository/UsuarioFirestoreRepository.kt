package com.example.mypet.repository

import com.example.mypet.entity.firestore.UsuarioFirestore
import com.example.mypet.firebase.FirestoreHelper

class UsuarioFirestoreRepository {

    private val usuariosRef = FirestoreHelper.db.collection("usuarios")

    fun guardarUsuario(usuario: UsuarioFirestore, onResult: (Boolean, String?) -> Unit) {
        usuariosRef.document(usuario.uid)
            .set(usuario)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun obtenerUsuario(uid: String, onResult: (UsuarioFirestore?, String?) -> Unit) {
        usuariosRef.document(uid)
            .get()
            .addOnSuccessListener { document ->
                val usuario = document.toObject(UsuarioFirestore::class.java)
                onResult(usuario, null)
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }

    fun actualizarUsuario(uid: String, data: Map<String, Any>, onResult: (Boolean, String?) -> Unit) {
        usuariosRef.document(uid)
            .update(data)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}