package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Recordatorio
import com.example.mypet.entity.mappers.RecordatorioDetalle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RecordatorioDAO(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    fun insert(recordatorio: Recordatorio): Long {
        val db = dbHelper.writableDatabase
        val now = getNow()

        val values = ContentValues().apply {
            put("FirestoreId", recordatorio.firestoreId)
            put("IdMascota", recordatorio.idMascota)
            put("Titulo", recordatorio.titulo)
            put("Descripcion", recordatorio.descripcion)
            put("IdTipoRecordatorio", recordatorio.idTipoRecordatorio)
            put("FechaInicio", recordatorio.fechaInicio)
            put("FechaFin", recordatorio.fechaFin)
            put("Frecuencia", recordatorio.frecuencia ?: "UNA_VEZ")
            put("FechaCreacion", recordatorio.fechaCreacion ?: now)
            put("UltimaModificacion", recordatorio.ultimaModificacion ?: now)
            put("Activo", if (recordatorio.activo) 1 else 0)
        }

        val result = db.insert("Recordatorio", null, values)
        db.close()
        return result
    }

    fun guardarOActualizar(recordatorio: Recordatorio): Long {
        val db = dbHelper.writableDatabase
        val now = getNow()

        val values = ContentValues().apply {
            put("FirestoreId", recordatorio.firestoreId)
            put("IdMascota", recordatorio.idMascota)
            put("Titulo", recordatorio.titulo)
            put("Descripcion", recordatorio.descripcion)
            put("IdTipoRecordatorio", recordatorio.idTipoRecordatorio)
            put("FechaInicio", recordatorio.fechaInicio)
            put("FechaFin", recordatorio.fechaFin)
            put("Frecuencia", recordatorio.frecuencia ?: "UNA_VEZ")
            put("FechaCreacion", recordatorio.fechaCreacion ?: now)
            put("UltimaModificacion", now)
            put("Activo", if (recordatorio.activo) 1 else 0)
        }

        val result: Long

        if (!recordatorio.firestoreId.isNullOrEmpty()) {
            val cursor = db.rawQuery(
                "SELECT IdRecordatorio FROM Recordatorio WHERE FirestoreId = ? LIMIT 1",
                arrayOf(recordatorio.firestoreId)
            )

            if (cursor.moveToFirst()) {
                val idExistente = cursor.getInt(cursor.getColumnIndexOrThrow("IdRecordatorio"))
                db.update(
                    "Recordatorio",
                    values,
                    "IdRecordatorio = ?",
                    arrayOf(idExistente.toString())
                )
                result = idExistente.toLong()
            } else {
                result = db.insert("Recordatorio", null, values)
            }

            cursor.close()
        } else {
            result = db.insert("Recordatorio", null, values)
        }

        db.close()
        return result
    }

    fun obtenerPorId(idRecordatorio: Int): Recordatorio? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Recordatorio WHERE IdRecordatorio = ? LIMIT 1",
            arrayOf(idRecordatorio.toString())
        )

        var recordatorio: Recordatorio? = null

        if (cursor.moveToFirst()) {
            recordatorio = cursorToRecordatorio(cursor)
        }

        cursor.close()
        db.close()
        return recordatorio
    }

    fun obtenerPorFirestoreId(firestoreId: String): Recordatorio? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Recordatorio WHERE FirestoreId = ? LIMIT 1",
            arrayOf(firestoreId)
        )

        var recordatorio: Recordatorio? = null

        if (cursor.moveToFirst()) {
            recordatorio = cursorToRecordatorio(cursor)
        }

        cursor.close()
        db.close()
        return recordatorio
    }

    fun obtenerPorMascota(idMascota: Int): List<Recordatorio> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Recordatorio>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Recordatorio WHERE IdMascota = ? ORDER BY FechaInicio ASC",
            arrayOf(idMascota.toString())
        )

        while (cursor.moveToNext()) {
            lista.add(cursorToRecordatorio(cursor))
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerActivosPorMascota(idMascota: Int): List<Recordatorio> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Recordatorio>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Recordatorio WHERE IdMascota = ? AND Activo = 1 ORDER BY FechaInicio ASC",
            arrayOf(idMascota.toString())
        )

        while (cursor.moveToNext()) {
            lista.add(cursorToRecordatorio(cursor))
        }

        cursor.close()
        db.close()
        return lista
    }

    fun desactivar(idRecordatorio: Int): Int {
        val db = dbHelper.writableDatabase
        val now = getNow()

        val values = ContentValues().apply {
            put("Activo", 0)
            put("UltimaModificacion", now)
        }

        val result = db.update(
            "Recordatorio",
            values,
            "IdRecordatorio = ?",
            arrayOf(idRecordatorio.toString())
        )

        db.close()
        return result
    }

    fun obtenerRecordatoriosConDetallePorMascota(idMascota: Int): List<RecordatorioDetalle> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<RecordatorioDetalle>()

        val query = """
            SELECT r.IdRecordatorio, r.FirestoreId, r.IdMascota,
                   m.Nombres AS NombreMascota,
                   r.Titulo, r.Descripcion,
                   r.IdTipoRecordatorio,
                   t.Nombre AS NombreTipoRecordatorio,
                   r.FechaInicio, r.FechaFin, r.Frecuencia,
                   r.Activo
            FROM Recordatorio r
            INNER JOIN Mascota m ON r.IdMascota = m.IdMascota
            INNER JOIN TipoRecordatorio t ON r.IdTipoRecordatorio = t.IdTipoRecordatorio
            WHERE r.IdMascota = ?
            ORDER BY r.FechaInicio ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(idMascota.toString()))

        while (cursor.moveToNext()) {
            lista.add(cursorToRecordatorioDetalle(cursor))
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerRecordatoriosPorUsuario(idUsuario: Int): List<RecordatorioDetalle> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<RecordatorioDetalle>()

        val query = """
            SELECT r.IdRecordatorio, r.FirestoreId, r.IdMascota,
                   m.Nombres AS NombreMascota,
                   r.Titulo, r.Descripcion,
                   r.IdTipoRecordatorio,
                   t.Nombre AS NombreTipoRecordatorio,
                   r.FechaInicio, r.FechaFin, r.Frecuencia,
                   r.Activo
            FROM Recordatorio r
            INNER JOIN Mascota m ON r.IdMascota = m.IdMascota
            INNER JOIN TipoRecordatorio t ON r.IdTipoRecordatorio = t.IdTipoRecordatorio
            WHERE m.IdUsuario = ? AND r.Activo = 1
            ORDER BY r.FechaInicio ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(idUsuario.toString()))

        while (cursor.moveToNext()) {
            lista.add(cursorToRecordatorioDetalle(cursor))
        }

        cursor.close()
        db.close()
        return lista
    }

    private fun cursorToRecordatorio(cursor: Cursor): Recordatorio {
        return Recordatorio(
            idRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdRecordatorio")),
            firestoreId = cursor.getString(cursor.getColumnIndexOrThrow("FirestoreId")),
            idMascota = cursor.getInt(cursor.getColumnIndexOrThrow("IdMascota")),
            titulo = cursor.getString(cursor.getColumnIndexOrThrow("Titulo")),
            descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion")),
            idTipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdTipoRecordatorio")),
            fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("FechaInicio")),
            fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("FechaFin")),
            frecuencia = cursor.getString(cursor.getColumnIndexOrThrow("Frecuencia")),
            fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow("FechaCreacion")),
            ultimaModificacion = cursor.getString(cursor.getColumnIndexOrThrow("UltimaModificacion")),
            activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
        )
    }

    private fun cursorToRecordatorioDetalle(cursor: Cursor): RecordatorioDetalle {
        return RecordatorioDetalle(
            idRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdRecordatorio")),
            firestoreId = cursor.getString(cursor.getColumnIndexOrThrow("FirestoreId")),
            idMascota = cursor.getInt(cursor.getColumnIndexOrThrow("IdMascota")),
            nombreMascota = cursor.getString(cursor.getColumnIndexOrThrow("NombreMascota")),
            titulo = cursor.getString(cursor.getColumnIndexOrThrow("Titulo")),
            descripcion = cursor.getString(cursor.getColumnIndexOrThrow("Descripcion")),
            idTipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdTipoRecordatorio")),
            nombreTipoRecordatorio = cursor.getString(cursor.getColumnIndexOrThrow("NombreTipoRecordatorio")),
            fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("FechaInicio")),
            fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("FechaFin")),
            frecuencia = cursor.getString(cursor.getColumnIndexOrThrow("Frecuencia")),
            activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
        )
    }

    private fun getNow(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
}