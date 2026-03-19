package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Especie

class EspecieDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)

// De momento no se considera
//    fun insert(especie: Especie) : Long {
//        val db = dbHelper.writableDatabase
//        val values = ContentValues().apply {
//            put("NombreEspecie", especie.nombreEspecie)
//        }
//        return db.insert("Especie", null, values)
//    }


    fun obtenerEspeciePorId(idEspecie : Int) : List<Especie> {
        val db = dbHelper.readableDatabase
        val especie = mutableListOf<Especie>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Especie WHERE IdEspecie = ?",
            arrayOf(idEspecie.toString())
        )
        while (cursor.moveToNext()) {
            especie.add(
                Especie(
                    idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                    nombreEspecie = cursor.getString(cursor.getColumnIndexOrThrow("NombreEspecie"))
                )
            )
        }
        cursor.close()
        db.close()
        return especie
    }
}