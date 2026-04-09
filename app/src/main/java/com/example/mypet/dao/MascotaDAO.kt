package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Mascota
import com.example.mypet.entity.mappers.MascotaDetalle

class MascotaDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)

    fun insert(mascota: Mascota): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IdUsuario", mascota.idUsuario)
            put("FirestoreId", mascota.firestoreId)
            put("Nombres", mascota.nombres)
            put("FechaNacimiento", mascota.fechaNacimiento)
            put("IdEspecie", mascota.idEspecie)
            put("IdRaza", mascota.idRaza)
            put("Sexo", mascota.sexo)
            put("PesoActual", mascota.pesoActual)
            put("EsEsterilizado", if (mascota.esEsterilizado) 1 else 0)
            put("TieneChip", if (mascota.tieneChip) 1 else 0)
            put("Notas", mascota.notas)
            put("Activo", if (mascota.activo) 1 else 0)
        }

        val result = db.insert("Mascota", null, values)
        db.close()
        return result
    }

    fun guardarOActualizar(mascota: Mascota): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IdUsuario", mascota.idUsuario)
            put("FirestoreId", mascota.firestoreId)
            put("Nombres", mascota.nombres)
            put("FechaNacimiento", mascota.fechaNacimiento)
            put("IdEspecie", mascota.idEspecie)
            put("IdRaza", mascota.idRaza)
            put("Sexo", mascota.sexo)
            put("PesoActual", mascota.pesoActual)
            put("EsEsterilizado", if (mascota.esEsterilizado) 1 else 0)
            put("TieneChip", if (mascota.tieneChip) 1 else 0)
            put("Notas", mascota.notas)
            put("Activo", if (mascota.activo) 1 else 0)
        }

        val result: Long

        if (!mascota.firestoreId.isNullOrEmpty()) {
            val cursor = db.rawQuery(
                "SELECT IdMascota FROM Mascota WHERE FirestoreId = ? LIMIT 1",
                arrayOf(mascota.firestoreId)
            )

            if (cursor.moveToFirst()) {
                val idMascotaExistente = cursor.getInt(cursor.getColumnIndexOrThrow("IdMascota"))
                db.update(
                    "Mascota",
                    values,
                    "IdMascota = ?",
                    arrayOf(idMascotaExistente.toString())
                )
                result = idMascotaExistente.toLong()
            } else {
                result = db.insert("Mascota", null, values)
            }

            cursor.close()
        } else {
            result = db.insert("Mascota", null, values)
        }

        db.close()
        return result
    }

    fun obtenerPorFirestoreId(firestoreId: String): Mascota? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Mascota WHERE FirestoreId = ? LIMIT 1",
            arrayOf(firestoreId)
        )

        var mascota: Mascota? = null

        if (cursor.moveToFirst()) {
            mascota = Mascota(
                idMascota = cursor.getInt(cursor.getColumnIndexOrThrow("IdMascota")),
                firestoreId = cursor.getString(cursor.getColumnIndexOrThrow("FirestoreId")),
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("IdUsuario")),
                nombres = cursor.getString(cursor.getColumnIndexOrThrow("Nombres")),
                fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("FechaNacimiento")),
                idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                idRaza = cursor.getInt(cursor.getColumnIndexOrThrow("IdRaza")),
                sexo = cursor.getString(cursor.getColumnIndexOrThrow("Sexo")),
                pesoActual = cursor.getString(cursor.getColumnIndexOrThrow("PesoActual")),
                esEsterilizado = cursor.getInt(cursor.getColumnIndexOrThrow("EsEsterilizado")) == 1,
                tieneChip = cursor.getInt(cursor.getColumnIndexOrThrow("TieneChip")) == 1,
                notas = cursor.getString(cursor.getColumnIndexOrThrow("Notas")),
                activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
            )
        }

        cursor.close()
        db.close()
        return mascota
    }

    fun obtenerPorId(idMascota: Int): Mascota? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Mascota WHERE IdMascota = ? LIMIT 1",
            arrayOf(idMascota.toString())
        )

        var mascota: Mascota? = null

        if (cursor.moveToFirst()) {
            mascota = Mascota(
                idMascota = cursor.getInt(cursor.getColumnIndexOrThrow("IdMascota")),
                firestoreId = cursor.getString(cursor.getColumnIndexOrThrow("FirestoreId")),
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("IdUsuario")),
                nombres = cursor.getString(cursor.getColumnIndexOrThrow("Nombres")),
                fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("FechaNacimiento")),
                idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                idRaza = cursor.getInt(cursor.getColumnIndexOrThrow("IdRaza")),
                sexo = cursor.getString(cursor.getColumnIndexOrThrow("Sexo")),
                pesoActual = cursor.getString(cursor.getColumnIndexOrThrow("PesoActual")),
                esEsterilizado = cursor.getInt(cursor.getColumnIndexOrThrow("EsEsterilizado")) == 1,
                tieneChip = cursor.getInt(cursor.getColumnIndexOrThrow("TieneChip")) == 1,
                notas = cursor.getString(cursor.getColumnIndexOrThrow("Notas")),
                activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
            )
        }

        cursor.close()
        db.close()
        return mascota
    }

    fun eliminarPorIdUsuario(idUsuario: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete("Mascota", "IdUsuario = ?", arrayOf(idUsuario.toString()))
        db.close()
        return result
    }

    fun obtenerMascotaPorId(idMascota: Int): List<Mascota> {
        val db = dbHelper.readableDatabase
        val mascota = mutableListOf<Mascota>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Mascota WHERE IdMascota = ?",
            arrayOf(idMascota.toString())
        )

        while (cursor.moveToNext()) {
            mascota.add(
                Mascota(
                    idMascota = cursor.getInt(cursor.getColumnIndexOrThrow("IdMascota")),
                    firestoreId = cursor.getString(cursor.getColumnIndexOrThrow("FirestoreId")),
                    idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("IdUsuario")),
                    nombres = cursor.getString(cursor.getColumnIndexOrThrow("Nombres")),
                    fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("FechaNacimiento")),
                    idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                    idRaza = cursor.getInt(cursor.getColumnIndexOrThrow("IdRaza")),
                    sexo = cursor.getString(cursor.getColumnIndexOrThrow("Sexo")),
                    pesoActual = cursor.getString(cursor.getColumnIndexOrThrow("PesoActual")),
                    esEsterilizado = cursor.getInt(cursor.getColumnIndexOrThrow("EsEsterilizado")) == 1,
                    tieneChip = cursor.getInt(cursor.getColumnIndexOrThrow("TieneChip")) == 1,
                    notas = cursor.getString(cursor.getColumnIndexOrThrow("Notas")),
                    activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
                )
            )
        }

        cursor.close()
        db.close()
        return mascota
    }

    fun obtenerMascotaPorIdUsuario(idUsuario: Int): List<Mascota> {
        val db = dbHelper.readableDatabase
        val mascota = mutableListOf<Mascota>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Mascota WHERE IdUsuario = ?",
            arrayOf(idUsuario.toString())
        )

        while (cursor.moveToNext()) {
            mascota.add(
                Mascota(
                    idMascota = cursor.getInt(cursor.getColumnIndexOrThrow("IdMascota")),
                    firestoreId = cursor.getString(cursor.getColumnIndexOrThrow("FirestoreId")),
                    idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("IdUsuario")),
                    nombres = cursor.getString(cursor.getColumnIndexOrThrow("Nombres")),
                    fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("FechaNacimiento")),
                    idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                    idRaza = cursor.getInt(cursor.getColumnIndexOrThrow("IdRaza")),
                    sexo = cursor.getString(cursor.getColumnIndexOrThrow("Sexo")),
                    pesoActual = cursor.getString(cursor.getColumnIndexOrThrow("PesoActual")),
                    esEsterilizado = cursor.getInt(cursor.getColumnIndexOrThrow("EsEsterilizado")) == 1,
                    tieneChip = cursor.getInt(cursor.getColumnIndexOrThrow("TieneChip")) == 1,
                    notas = cursor.getString(cursor.getColumnIndexOrThrow("Notas")),
                    activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
                )
            )
        }

        cursor.close()
        db.close()
        return mascota
    }

    fun obtenerMascotasConDetalle(idUsuario: Int): List<MascotaDetalle> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<MascotaDetalle>()

        val query = """
            SELECT m.IdMascota, m.FirestoreId, m.IdUsuario, m.Nombres, m.FechaNacimiento,
                   m.IdEspecie, e.NombreEspecie,
                   m.IdRaza, r.NombreRaza,
                   m.Sexo, m.PesoActual, m.EsEsterilizado, m.TieneChip,
                   m.Notas, m.Activo
            FROM Mascota m
            INNER JOIN Especie e ON m.IdEspecie = e.IdEspecie
            INNER JOIN Raza r ON m.IdRaza = r.IdRaza
            WHERE m.IdUsuario = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(idUsuario.toString()))

        while (cursor.moveToNext()) {
            lista.add(
                MascotaDetalle(
                    idMascota = cursor.getInt(cursor.getColumnIndexOrThrow("IdMascota")),
                    firestoreId = cursor.getString(cursor.getColumnIndexOrThrow("FirestoreId")),
                    idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("IdUsuario")),
                    nombres = cursor.getString(cursor.getColumnIndexOrThrow("Nombres")),
                    fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("FechaNacimiento")),
                    idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                    nombreEspecie = cursor.getString(cursor.getColumnIndexOrThrow("NombreEspecie")),
                    idRaza = cursor.getInt(cursor.getColumnIndexOrThrow("IdRaza")),
                    nombreRaza = cursor.getString(cursor.getColumnIndexOrThrow("NombreRaza")),
                    sexo = cursor.getString(cursor.getColumnIndexOrThrow("Sexo")),
                    pesoActual = cursor.getString(cursor.getColumnIndexOrThrow("PesoActual")),
                    esEsterilizado = cursor.getInt(cursor.getColumnIndexOrThrow("EsEsterilizado")) == 1,
                    tieneChip = cursor.getInt(cursor.getColumnIndexOrThrow("TieneChip")) == 1,
                    notas = cursor.getString(cursor.getColumnIndexOrThrow("Notas")),
                    activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
                )
            )
        }

        cursor.close()
        db.close()
        return lista
    }
}