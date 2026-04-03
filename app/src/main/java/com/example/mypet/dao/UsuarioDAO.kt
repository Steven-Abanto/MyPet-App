package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Usuario

class UsuarioDAO(context: Context) {
    private val dbHelper = AppDatabaseHelper(context)
    fun insert(usuario: Usuario): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("FirebaseUid", usuario.firebaseUid)
            put("Nombres", usuario.nombres)
            put("ApellidoPaterno", usuario.apellidoPaterno)
            put("ApellidoMaterno", usuario.apellidoMaterno)
            put("Email", usuario.email)
            put("Telefono", usuario.telefono)
            put("FechaNacimiento", usuario.fechaNacimiento)
            put("Pronombre", usuario.pronombre)
            put("Activo", if (usuario.activo) 1 else 0)
        }
        val result = db.insert("Usuario", null, values)
        db.close()
        return result
    }

    fun guardarOActualizar(usuario: Usuario): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("FirebaseUid", usuario.firebaseUid)
            put("Nombres", usuario.nombres)
            put("ApellidoPaterno", usuario.apellidoPaterno)
            put("ApellidoMaterno", usuario.apellidoMaterno)
            put("Email", usuario.email)
            put("Telefono", usuario.telefono)
            put("FechaNacimiento", usuario.fechaNacimiento)
            put("Pronombre", usuario.pronombre)
            put("Activo", if (usuario.activo) 1 else 0)
        }

        val result = db.insertWithOnConflict(
            "Usuario",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )

        db.close()
        return result
    }

    fun obtenerPorFirebaseUid(firebaseUid: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Usuario WHERE FirebaseUid = ? LIMIT 1",
            arrayOf(firebaseUid)
        )

        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            usuario = Usuario(
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("IdUsuario")),
                firebaseUid = cursor.getString(cursor.getColumnIndexOrThrow("FirebaseUid")),
                nombres = cursor.getString(cursor.getColumnIndexOrThrow("Nombres")),
                apellidoPaterno = cursor.getString(cursor.getColumnIndexOrThrow("ApellidoPaterno")),
                apellidoMaterno = cursor.getString(cursor.getColumnIndexOrThrow("ApellidoMaterno")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("Email")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("Telefono")) ?: "",
                fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("FechaNacimiento")) ?: "",
                pronombre = cursor.getString(cursor.getColumnIndexOrThrow("Pronombre")) ?: "",
                activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
            )
        }

        cursor.close()
        db.close()
        return usuario
    }

    fun obtenerUsuarioPorId(idUsuario: Int): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Usuario WHERE IdUsuario = ? LIMIT 1",
            arrayOf(idUsuario.toString())
        )

        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            usuario = Usuario(
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("IdUsuario")),
                firebaseUid = cursor.getString(cursor.getColumnIndexOrThrow("FirebaseUid")),
                nombres = cursor.getString(cursor.getColumnIndexOrThrow("Nombres")),
                apellidoPaterno = cursor.getString(cursor.getColumnIndexOrThrow("ApellidoPaterno")),
                apellidoMaterno = cursor.getString(cursor.getColumnIndexOrThrow("ApellidoMaterno")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("Email")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("Telefono")) ?: "",
                fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("FechaNacimiento")) ?: "",
                pronombre = cursor.getString(cursor.getColumnIndexOrThrow("Pronombre")) ?: "",
                activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
            )
        }

        cursor.close()
        db.close()
        return usuario
    }
}