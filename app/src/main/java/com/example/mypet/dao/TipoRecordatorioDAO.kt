package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.TipoRecordatorio

class TipoRecordatorioDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)

// De momento no se considera
//    fun insert(tipoRecordatorio: TipoRecordatorio) : Long {
//        val db = dbHelper.writableDatabase
//        val values = ContentValues().apply {
//            put("Nombre", tipoRecordatorio.nombre)
//            put("EsMedico", tipoRecordatorio.esMedico)
//        }
//        return db.insert("TipoRecordatorio", null, values)
//    }


    fun obtenerTipoRecordatorioPorId(idTipoRecordatorio : Int) : List<TipoRecordatorio> {
        val db = dbHelper.readableDatabase
        val tipoRecordatorio = mutableListOf<TipoRecordatorio>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM TipoRecordatorio WHERE IdTipoRecordatorio = ?",
            arrayOf(idTipoRecordatorio.toString())
        )
        while (cursor.moveToNext()) {
            tipoRecordatorio.add(
                TipoRecordatorio(
                    idTipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdTipoRecordatorio")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre")),
                    esMedico = cursor.getInt(cursor.getColumnIndexOrThrow("EsMedico")) == 1
                )
            )
        }
        cursor.close()
        db.close()
        return tipoRecordatorio
    }
}