package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.HistorialRecordatorio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HistorialRecordatorioDAO(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    fun insert(historial: HistorialRecordatorio): Long {
        val db = dbHelper.writableDatabase
        val now = getNow()

        val values = ContentValues().apply {
            put("FirestoreId", historial.firestoreId)
            put("IdRecordatorio", historial.idRecordatorio)
            put("FechaProgramada", historial.fechaProgramada)
            put("FechaCompletado", historial.fechaCompletado)
            put("Notas", historial.notas)
            put("Estado", historial.estado)
            put("FechaCreacion", historial.fechaCreacion ?: now)
            put("UltimaModificacion", historial.ultimaModificacion ?: now)
            put("Activo", if (historial.activo) 1 else 0)
        }

        val result = db.insert("Historial_Recordatorio", null, values)
        db.close()
        return result
    }

    fun guardarOActualizar(historial: HistorialRecordatorio): Long {
        val db = dbHelper.writableDatabase
        val now = getNow()

        val values = ContentValues().apply {
            put("FirestoreId", historial.firestoreId)
            put("IdRecordatorio", historial.idRecordatorio)
            put("FechaProgramada", historial.fechaProgramada)
            put("FechaCompletado", historial.fechaCompletado)
            put("Notas", historial.notas)
            put("Estado", historial.estado)
            put("FechaCreacion", historial.fechaCreacion ?: now)
            put("UltimaModificacion", now)
            put("Activo", if (historial.activo) 1 else 0)
        }

        val result: Long

        if (!historial.firestoreId.isNullOrEmpty()) {
            val cursor = db.rawQuery(
                "SELECT IdHistorial FROM Historial_Recordatorio WHERE FirestoreId = ? LIMIT 1",
                arrayOf(historial.firestoreId)
            )

            if (cursor.moveToFirst()) {
                val idExistente = cursor.getInt(cursor.getColumnIndexOrThrow("IdHistorial"))
                db.update(
                    "Historial_Recordatorio",
                    values,
                    "IdHistorial = ?",
                    arrayOf(idExistente.toString())
                )
                result = idExistente.toLong()
            } else {
                result = db.insert("Historial_Recordatorio", null, values)
            }

            cursor.close()
        } else {
            result = db.insert("Historial_Recordatorio", null, values)
        }

        db.close()
        return result
    }

    fun obtenerPorId(idHistorial: Int): HistorialRecordatorio? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Historial_Recordatorio WHERE IdHistorial = ? LIMIT 1",
            arrayOf(idHistorial.toString())
        )

        var historial: HistorialRecordatorio? = null

        if (cursor.moveToFirst()) {
            historial = cursorToHistorial(cursor)
        }

        cursor.close()
        db.close()
        return historial
    }

    fun obtenerPorRecordatorio(idRecordatorio: Int): List<HistorialRecordatorio> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<HistorialRecordatorio>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Historial_Recordatorio WHERE IdRecordatorio = ? ORDER BY FechaProgramada DESC",
            arrayOf(idRecordatorio.toString())
        )

        while (cursor.moveToNext()) {
            lista.add(cursorToHistorial(cursor))
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerPorMascota(idMascota: Int): List<HistorialRecordatorio> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<HistorialRecordatorio>()

        val query = """
            SELECT h.*
            FROM Historial_Recordatorio h
            INNER JOIN Recordatorio r ON h.IdRecordatorio = r.IdRecordatorio
            WHERE r.IdMascota = ?
            ORDER BY h.FechaProgramada DESC
        """.trimIndent()

        val cursor: Cursor = db.rawQuery(query, arrayOf(idMascota.toString()))

        while (cursor.moveToNext()) {
            lista.add(cursorToHistorial(cursor))
        }

        cursor.close()
        db.close()
        return lista
    }

    private fun cursorToHistorial(cursor: Cursor): HistorialRecordatorio {
        return HistorialRecordatorio(
            idHistorial = cursor.getInt(cursor.getColumnIndexOrThrow("IdHistorial")),
            firestoreId = cursor.getString(cursor.getColumnIndexOrThrow("FirestoreId")),
            idRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdRecordatorio")),
            fechaProgramada = cursor.getString(cursor.getColumnIndexOrThrow("FechaProgramada")),
            fechaCompletado = cursor.getString(cursor.getColumnIndexOrThrow("FechaCompletado")),
            notas = cursor.getString(cursor.getColumnIndexOrThrow("Notas")),
            estado = cursor.getString(cursor.getColumnIndexOrThrow("Estado")),
            fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow("FechaCreacion")),
            ultimaModificacion = cursor.getString(cursor.getColumnIndexOrThrow("UltimaModificacion")),
            activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
        )
    }

    private fun getNow(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
}