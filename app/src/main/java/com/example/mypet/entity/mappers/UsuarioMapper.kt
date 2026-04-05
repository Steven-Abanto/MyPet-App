package com.example.mypet.entity.mappers

import com.example.mypet.entity.Usuario
import com.example.mypet.entity.firestore.UsuarioFirestore

object UsuarioMapper {

    fun toLocal(usuarioFirestore: UsuarioFirestore): Usuario {
        return Usuario(
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
    }

    fun toFirestore(usuario: Usuario): UsuarioFirestore {
        return UsuarioFirestore(
            uid = usuario.firebaseUid,
            nombres = usuario.nombres,
            apellidoPaterno = usuario.apellidoPaterno,
            apellidoMaterno = usuario.apellidoMaterno,
            email = usuario.email,
            telefono = usuario.telefono,
            fechaNacimiento = usuario.fechaNacimiento,
            pronombre = usuario.pronombre,
            activo = usuario.activo
        )
    }
}