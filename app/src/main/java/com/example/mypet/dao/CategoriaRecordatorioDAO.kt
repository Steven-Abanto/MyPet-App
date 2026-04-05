package com.example.mypet.dao

import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.CategoriaRecordatorio

class CategoriaRecordatorioDAO(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    fun listarTodas(): List<CategoriaRecordatorio> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<CategoriaRecordatorio>()

        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM CategoriaRecordatorio ORDER BY IdCategoriaRecordatorio ASC",
            null
        )

        while (cursor.moveToNext()) {
            lista.add(
                CategoriaRecordatorio(
                    idCategoriaRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdCategoriaRecordatorio")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"))
                )
            )
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerPorId(idCategoriaRecordatorio: Int): CategoriaRecordatorio? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM CategoriaRecordatorio WHERE IdCategoriaRecordatorio = ? LIMIT 1",
            arrayOf(idCategoriaRecordatorio.toString())
        )

        var categoria: CategoriaRecordatorio? = null

        if (cursor.moveToFirst()) {
            categoria = CategoriaRecordatorio(
                idCategoriaRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdCategoriaRecordatorio")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"))
            )
        }

        cursor.close()
        db.close()
        return categoria
    }

    fun obtenerPorNombre(nombre: String): CategoriaRecordatorio? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM CategoriaRecordatorio WHERE Nombre = ? LIMIT 1",
            arrayOf(nombre)
        )

        var categoria: CategoriaRecordatorio? = null

        if (cursor.moveToFirst()) {
            categoria = CategoriaRecordatorio(
                idCategoriaRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow("IdCategoriaRecordatorio")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("Nombre"))
            )
        }

        cursor.close()
        db.close()
        return categoria
    }
}