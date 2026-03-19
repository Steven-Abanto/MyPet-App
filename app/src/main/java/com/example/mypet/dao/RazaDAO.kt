package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Raza

class RazaDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)

// De momento no se considera
//    fun insert(raza: Raza) : Long {
//        val db = dbHelper.writableDatabase
//        val values = ContentValues().apply {
//            put("NombreRaza", raza.nombreRaza)
//        }
//        return db.insert("Raza", null, values)
//    }


    fun obtenerRazaPorId(idRaza : Int) : List<Raza> {
        val db = dbHelper.readableDatabase
        val raza = mutableListOf<Raza>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Raza WHERE IdRaza = ?",
            arrayOf(idRaza.toString())
        )
        while (cursor.moveToNext()) {
            raza.add(
                Raza(
                    idRaza = cursor.getInt(cursor.getColumnIndexOrThrow("IdRaza")),
                    idEspecie = cursor.getInt(cursor.getColumnIndexOrThrow("IdEspecie")),
                    nombreRaza = cursor.getString(cursor.getColumnIndexOrThrow("NombreRaza"))
                )
            )
        }
        cursor.close()
        db.close()
        return raza
    }
}