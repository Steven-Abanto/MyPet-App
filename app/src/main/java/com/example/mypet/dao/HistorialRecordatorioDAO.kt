package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Especie
import com.example.mypet.entity.HistorialRecordatorio
import com.example.mypet.entity.Recordatorio

class HistorialRecordatorioDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)

    fun insert(historialRecordatorio: HistorialRecordatorio) : Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IdRecordatorio", historialRecordatorio.idRecordatorio)
            put("FechaInicio", historialRecordatorio.fechaInicio)
            put("FechaFin", historialRecordatorio.fechaFin)
            put("FechaCompletado", historialRecordatorio.fechaCompletado)
            put("Notas", historialRecordatorio.notas)
            put("Estado", historialRecordatorio.estado)
        }
        return db.insert("Recordatorio", null, values)
    }
}