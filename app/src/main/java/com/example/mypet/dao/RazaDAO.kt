package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Raza

class RazaDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)

    fun obtenerRazas(): List<Raza> {
        val db = dbHelper.readableDatabase
        val razas = mutableListOf<Raza>()
        val cursor: Cursor = db.rawQuery(
            "SELECT IdRaza, IdEspecie, NombreRaza FROM Raza ORDER BY IdEspecie, NombreRaza",
            null
        )

        while (cursor.moveToNext()) {
            razas.add(
                Raza(
                    idRaza = cursor.getInt(cursor.getColumnIndexOrThrow("IdRaza")),
                    idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                    nombreRaza = cursor.getString(cursor.getColumnIndexOrThrow("NombreRaza"))
                )
            )
        }

        cursor.close()
        db.close()
        return razas
    }

    fun obtenerRazaPorId(idRaza: String): Raza? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT IdRaza, IdEspecie, NombreRaza FROM Raza WHERE IdRaza = ?",
            arrayOf(idRaza)
        )

        val raza = if (cursor.moveToFirst()) {
            Raza(
                idRaza = cursor.getInt(cursor.getColumnIndexOrThrow("IdRaza")),
                idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                nombreRaza = cursor.getString(cursor.getColumnIndexOrThrow("NombreRaza"))
            )
        } else {
            null
        }

        cursor.close()
        db.close()
        return raza
    }

    fun obtenerRazaPorIdEspecie(idEspecie: Int): List<Raza> {
        val db = dbHelper.readableDatabase
        val razas = mutableListOf<Raza>()
        val cursor: Cursor = db.rawQuery(
            "SELECT IdRaza, IdEspecie, NombreRaza FROM Raza WHERE IdEspecie = ? ORDER BY NombreRaza",
            arrayOf(idEspecie.toString())
        )

        while (cursor.moveToNext()) {
            razas.add(
                Raza(
                    idRaza = cursor.getInt(cursor.getColumnIndexOrThrow("IdRaza")),
                    idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                    nombreRaza = cursor.getString(cursor.getColumnIndexOrThrow("NombreRaza"))
                )
            )
        }

        cursor.close()
        db.close()
        return razas
    }

    fun insertarRaza(raza: Raza): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IdRaza", raza.idRaza)
            put("IdEspecie", raza.idEspecie)
            put("NombreRaza", raza.nombreRaza)
        }

        val result = db.insertWithOnConflict(
            "Raza",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        db.close()
        return result
    }

    fun limpiarRazas() {
        val db = dbHelper.writableDatabase
        db.delete("Raza", null, null)
        db.close()
    }
}