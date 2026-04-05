package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.TipoRecordatorio

class TipoRecordatorioDAO(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    fun insert(tipo: TipoRecordatorio): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IdCategoriaRecordatorio", tipo.idCategoriaRecordatorio)
            put("Nombre", tipo.nombre)
        }

        val result = db.insert("TipoRecordatorio", null, values)
        db.close()
        return result
    }

    fun guardarOActualizar(tipo: TipoRecordatorio): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("IdCategoriaRecordatorio", tipo.idCategoriaRecordatorio)
            put("Nombre", tipo.nombre)
        }

        val cursor = db.rawQuery(
            """
            SELECT IdTipoRecordatorio
            FROM TipoRecordatorio
            WHERE IdCategoriaRecordatorio = ? AND Nombre = ?
            LIMIT 1
            """.trimIndent(),
            arrayOf(tipo.idCategoriaRecordatorio.toString(), tipo.nombre)
        )

        val result: Long

        if (cursor.moveToFirst()) {
            val idExistente = cursor.getInt(cursor.getColumnIndexOrThrow("IdTipoRecordatorio"))

            db.update(
                "TipoRecordatorio",
                values,
                "IdTipoRecordatorio = ?",
                arrayOf(idExistente.toString())
            )

            result = idExistente.toLong()
        } else {
            result = db.insert("TipoRecordatorio", null, values)
        }

        cursor.close()
        db.close()
        return result
    }

    fun listarPorCategoria(idCategoriaRecordatorio: Int): List<TipoRecordatorio> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<TipoRecordatorio>()

        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM TipoRecordatorio WHERE IdCategoriaRecordatorio = ? ORDER BY Nombre ASC",
            arrayOf(idCategoriaRecordatorio.toString())
        )

        while (cursor.moveToNext()) {
            lista.add(
                TipoRecordatorio(
                    idTipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdTipoRecordatorio")),
                    idCategoriaRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdCategoriaRecordatorio")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"))
                )
            )
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerPorId(idTipoRecordatorio: Int): TipoRecordatorio? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM TipoRecordatorio WHERE IdTipoRecordatorio = ? LIMIT 1",
            arrayOf(idTipoRecordatorio.toString())
        )

        var tipo: TipoRecordatorio? = null

        if (cursor.moveToFirst()) {
            tipo = TipoRecordatorio(
                idTipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdTipoRecordatorio")),
                idCategoriaRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdCategoriaRecordatorio")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"))
            )
        }

        cursor.close()
        db.close()
        return tipo
    }

    fun obtenerPorNombreYCategoria(nombre: String, idCategoriaRecordatorio: Int): TipoRecordatorio? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            """
            SELECT * FROM TipoRecordatorio
            WHERE Nombre = ? AND IdCategoriaRecordatorio = ?
            LIMIT 1
            """.trimIndent(),
            arrayOf(nombre, idCategoriaRecordatorio.toString())
        )

        var tipo: TipoRecordatorio? = null

        if (cursor.moveToFirst()) {
            tipo = TipoRecordatorio(
                idTipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdTipoRecordatorio")),
                idCategoriaRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdCategoriaRecordatorio")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"))
            )
        }

        cursor.close()
        db.close()
        return tipo
    }
}