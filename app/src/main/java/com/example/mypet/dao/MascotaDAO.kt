package com.example.mypet.dao


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Mascota
import com.example.mypet.entity.MascotaDetalle


class MascotaDAO(context : Context) {
    private val dbHelper = AppDatabaseHelper(context)
    fun insert(mascota: Mascota) : Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IdUsuario", mascota.idUsuario)
            put("Nombres", mascota.nombres)
            put("FechaNacimiento", mascota.fechaNacimiento)
            put("IdEspecie", mascota.idEspecie)
            put("IdRaza", mascota.idRaza)
            put("Sexo", mascota.sexo)
            put("PesoActual", mascota.pesoActual)
            put("EsEsterilizado", mascota.esEsterilizado)
            put("TieneChip", mascota.tieneChip)
            put("Notas", mascota.notas)
            put("Activo", mascota.activo)
        }
        return db.insert("Mascota", null, values)
    }


    fun obtenerMascotaPorId(idMascota : Int) : List<Mascota> {
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

    fun obtenerMascotaPorIdUsuario(idUsuario : Int) : List<Mascota> {
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
        SELECT m.IdMascota, m.IdUsuario, m.Nombres, m.FechaNacimiento,
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

