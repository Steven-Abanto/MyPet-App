package com.example.mypet.entity.mappers

import com.example.mypet.entity.Mascota
import com.example.mypet.entity.firestore.MascotaFirestore

object MascotaMapper {

    fun toLocal(
        firestoreId: String,
        idUsuarioLocal: Int,
        mascotaFirestore: MascotaFirestore
    ): Mascota {
        return Mascota(
            idMascota = 0,
            firestoreId = firestoreId,
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
    }

    fun toFirestore(
        firebaseUid: String,
        mascota: Mascota
    ): MascotaFirestore {
        return MascotaFirestore(
            firebaseUid = firebaseUid,
            nombres = mascota.nombres,
            fechaNacimiento = mascota.fechaNacimiento,
            idEspecie = mascota.idEspecie,
            idRaza = mascota.idRaza,
            sexo = mascota.sexo,
            pesoActual = mascota.pesoActual,
            esEsterilizado = mascota.esEsterilizado,
            tieneChip = mascota.tieneChip,
            notas = mascota.notas,
            activo = mascota.activo
        )
    }
}