package com.example.mypet.repository

import android.content.Context
import com.example.mypet.dao.UsuarioDAO
import com.example.mypet.entity.Usuario
import com.example.mypet.entity.firestore.UsuarioFirestore
import com.example.mypet.entity.mappers.UsuarioMapper
import com.example.mypet.firebase.FirestoreHelper
import com.google.firebase.auth.FirebaseUser

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
                val usuarioLocal = UsuarioMapper.toLocal(usuarioFirestore)
                usuarioDAO.guardarOActualizar(usuarioLocal)
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    fun registrarOSincronizarUsuarioGoogle(
        context: Context,
        firebaseUser: FirebaseUser,
        onResult: (Boolean, String?) -> Unit
    ) {
        val uid = firebaseUser.uid
        val email = firebaseUser.email ?: ""

        obtenerUsuario(uid) { usuarioFirestore, error ->
            if (error != null) {
                onResult(false, error)
                return@obtenerUsuario
            }

            if (usuarioFirestore != null) {
                try {
                    val usuarioDAO = UsuarioDAO(context)
                    val usuarioLocal = UsuarioMapper.toLocal(usuarioFirestore)
                    usuarioDAO.guardarOActualizar(usuarioLocal)
                    onResult(true, null)
                } catch (e: Exception) {
                    onResult(false, e.message)
                }
                return@obtenerUsuario
            }

            val displayName = firebaseUser.displayName?.trim().orEmpty()
            val partesNombre = displayName.split("\\s+".toRegex()).filter { it.isNotBlank() }

            val nombres = when {
                partesNombre.isEmpty() -> "Usuario Google"
                partesNombre.size == 1 -> partesNombre[0]
                else -> partesNombre.dropLast(2).joinToString(" ").ifBlank { partesNombre.first() }
            }

            val apellidoPaterno = when {
                partesNombre.size >= 2 -> partesNombre[partesNombre.size - 2]
                else -> ""
            }

            val apellidoMaterno = when {
                partesNombre.size >= 3 -> partesNombre.last()
                else -> ""
            }

            val nuevoUsuarioFirestore = UsuarioFirestore(
                uid = uid,
                nombres = nombres,
                apellidoPaterno = apellidoPaterno,
                apellidoMaterno = apellidoMaterno,
                email = email,
                telefono = "",
                fechaNacimiento = "",
                pronombre = "",
                activo = true
            )

            guardarUsuario(nuevoUsuarioFirestore) { okGuardar, errorGuardar ->
                if (!okGuardar) {
                    onResult(false, errorGuardar)
                    return@guardarUsuario
                }

                try {
                    val usuarioDAO = UsuarioDAO(context)

                    val usuarioLocal = Usuario(
                        firebaseUid = uid,
                        nombres = nuevoUsuarioFirestore.nombres,
                        apellidoPaterno = nuevoUsuarioFirestore.apellidoPaterno,
                        apellidoMaterno = nuevoUsuarioFirestore.apellidoMaterno,
                        email = nuevoUsuarioFirestore.email,
                        telefono = nuevoUsuarioFirestore.telefono,
                        fechaNacimiento = nuevoUsuarioFirestore.fechaNacimiento,
                        pronombre = nuevoUsuarioFirestore.pronombre,
                        activo = nuevoUsuarioFirestore.activo
                    )

                    usuarioDAO.guardarOActualizar(usuarioLocal)
                    onResult(true, null)
                } catch (e: Exception) {
                    onResult(false, e.message)
                }
            }
        }
    }
}