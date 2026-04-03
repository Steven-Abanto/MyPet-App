package com.example.mypet.repository

import android.content.Context
import com.example.mypet.dao.UsuarioDAO
import com.example.mypet.entity.Usuario
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

    fun sincronizarUsuarioALocal(
        context: Context,
        uid: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        obtenerUsuario(uid) { usuarioFirestore, error ->
            if (error != null) {
                onResult(false, error)
                return@obtenerUsuario
            }

            if (usuarioFirestore == null) {
                onResult(false, "No se encontró el usuario en Firestore")
                return@obtenerUsuario
            }

            try {
                val usuarioDAO = UsuarioDAO(context)

                val usuarioLocal = Usuario(
                    idUsuario = 0,
                    firebaseUid = usuarioFirestore.uid,
                    nombres = usuarioFirestore.nombres,
                    apellidoPaterno = usuarioFirestore.apellidoPaterno,
                    apellidoMaterno = usuarioFirestore.apellidoMaterno,
                    email = usuarioFirestore.email,
                    telefono = usuarioFirestore.telefono,
                    fechaNacimiento = usuarioFirestore.fechaNacimiento,
                    pronombre = usuarioFirestore.pronombre,
                    activo = usuarioFirestore.activo
                )

                usuarioDAO.guardarOActualizar(usuarioLocal)
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}