package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Especie

class EspecieDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)

    fun obtenerEspecies(): List<Especie> {
        val db = dbHelper.readableDatabase
        val especies = mutableListOf<Especie>()
        val cursor: Cursor = db.rawQuery(
            "SELECT IdEspecie, NombreEspecie FROM Especie ORDER BY NombreEspecie",
            null
        )

        while (cursor.moveToNext()) {
            especies.add(
                Especie(
                    idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                    nombreEspecie = cursor.getString(cursor.getColumnIndexOrThrow("NombreEspecie"))
                )
            )
        }

        cursor.close()
        db.close()
        return especies
    }

    fun obtenerEspeciePorId(idEspecie: String): Especie? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT IdEspecie, NombreEspecie FROM Especie WHERE IdEspecie = ?",
            arrayOf(idEspecie)
        )

        val especie = if (cursor.moveToFirst()) {
            Especie(
                idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                nombreEspecie = cursor.getString(cursor.getColumnIndexOrThrow("NombreEspecie"))
            )
        } else {
            null
        }

        cursor.close()
        db.close()
        return especie
    }

    fun insertarEspecie(especie: Especie): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IdEspecie", especie.idEspecie)
            put("NombreEspecie", especie.nombreEspecie)
        }

        val result = db.insertWithOnConflict(
            "Especie",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        db.close()
        return result
    }

    fun limpiarEspecies() {
        val db = dbHelper.writableDatabase
        db.delete("Especie", null, null)
        db.close()
    }
}