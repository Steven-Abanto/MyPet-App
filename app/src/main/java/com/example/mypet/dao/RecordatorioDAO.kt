package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Recordatorio

class RecordatorioDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)

    fun insert(recordatorio: Recordatorio) : Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IdUsuario", recordatorio.idUsuario)
            put("IdMascota", recordatorio.idMascota)
            put("Titulo", recordatorio.titulo)
            put("Descripcion", recordatorio.descripcion)
            put("IdTipoRecordatorio", recordatorio.idTipoRecordatorio)
            put("FechaInicio", recordatorio.fechaInicio)
            put("FechaFin", recordatorio.fechaFin)
            put("SeRepite", recordatorio.seRepite)
            put("Frecuencia", recordatorio.frecuencia)
            put("FechaCreacion", recordatorio.fechaCreacion)
            put("Activo", recordatorio.activo)
        }
        return db.insert("Recordatorio", null, values)
    }


    fun obtenerRecordatorioPorId(idRecordatorio : Int) : List<Recordatorio> {
        val db = dbHelper.readableDatabase
        val recordatorio = mutableListOf<Recordatorio>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Recordatorio WHERE IdRecordatorio = ?",
            arrayOf(idRecordatorio.toString())
        )
        while (cursor.moveToNext()) {
            recordatorio.add(
                Recordatorio(
                    idRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdRecordatorio")),
                    idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("IdUsuario")),
                    idMascota = cursor.getInt(cursor.getColumnIndexOrThrow("IdMascota")),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow("Titulo")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion")),
                    idTipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdTipoRecordatorio")),
                    fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("FechaInicio")),
                    fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("FechaFin")),
                    seRepite = cursor.getInt(cursor.getColumnIndexOrThrow("SeRepite")) == 1,
                    frecuencia = cursor.getString(cursor.getColumnIndexOrThrow("Frecuencia")),
                    fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow("FechaCreacion")),
                    activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
                )
            )
        }
        cursor.close()
        db.close()
        return recordatorio
    }
}