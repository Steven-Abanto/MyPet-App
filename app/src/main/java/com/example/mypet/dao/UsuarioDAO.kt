package com.example.mypet.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.entity.Usuario

class UsuarioDAO(context : Context) {
    private val dbHelper = AppDatabaseHelper(context)
    fun insert(usuario: Usuario) : Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("Nombres", usuario.nombres)
            put("Apellidos", usuario.apellidoPaterno)
            put("ApellidoMaterno", usuario.apellidoMaterno)
            put("Email", usuario.email)
            put("Telefono", usuario.telefono)
            put("FechaNacimiento", usuario.fechaNacimiento)
            put("Pronombre", usuario.pronombre)
            put("ContrasenaHashed", usuario.contrasenaHashed)
            put("FechaCreacion", usuario.fechaCreacion)
            put("Activo", usuario.activo)
        }
        return db.insert("Usuario", null, values)
    }

    fun obtenerUsuarioPorId(idUsuario : Int) : List<Usuario> {
        val db = dbHelper.readableDatabase
        val usuario = mutableListOf<Usuario>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Usuario WHERE IdUsuario = ?",
            arrayOf(idUsuario.toString())
        )
        while (cursor.moveToNext()) {
            usuario.add(
                Usuario(
                    idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("IdUsuario")),
                    nombres = cursor.getString(cursor.getColumnIndexOrThrow("Nombres")),
                    apellidoPaterno = cursor.getString(cursor.getColumnIndexOrThrow("ApellidoPaterno")),
                    apellidoMaterno = cursor.getString(cursor.getColumnIndexOrThrow("ApellidoMaterno")),
                    email = cursor.getString(cursor.getColumnIndexOrThrow("Email")),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow("Telefono")),
                    fechaNacimiento = cursor.getString(cursor.getColumnIndexOrThrow("FechaNacimiento")),
                    pronombre = cursor.getString(cursor.getColumnIndexOrThrow("Pronombre")),
                    contrasenaHashed = cursor.getString(cursor.getColumnIndexOrThrow("ContrasenaHashed")),
                    fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow("FechaCreacion")),
                    activo = cursor.getInt(cursor.getColumnIndexOrThrow("Activo")) == 1
                )
            )
        }
        cursor.close()
        db.close()
        return usuario
    }
}