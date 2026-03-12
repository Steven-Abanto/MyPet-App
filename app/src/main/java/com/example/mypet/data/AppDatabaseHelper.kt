package com.example.idatdemo.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context : Context) : SQLiteOpenHelper(context, "mypet.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        // Tabla: Usuario
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Usuario (
                IdUsuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                Nombres TEXT NOT NULL,
                ApellidoPaterno TEXT NOT NULL,
                ApellidoMaterno TEXT NOT NULL,
                Email TEXT NOT NULL UNIQUE,
                Telefono TEXT,
                FechaNacimiento DATE,
                Pronombre TEXT,
                ContrasenaHashed TEXT NOT NULL,
                FechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                Activo BOOLEAN DEFAULT TRUE
            )
        """.trimIndent())

        // Tabla: Especie
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Especie (
                IdEspecie INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                NombreEspecie TEXT NOT NULL UNIQUE
            )
        """.trimIndent())

        // Tabla: Raza (depende de la especie)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Raza (
                IdRaza INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                IdEspecie INTEGER NOT NULL,
                NombreRaza TEXT NOT NULL,

                CONSTRAINT fk_raza_especie
                    FOREIGN KEY (IdEspecie)
                    REFERENCES Especie(IdEspecie)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE,

                UNIQUE (IdEspecie, NombreRaza)
            )
        """.trimIndent())

        // Tabla: Mascota (Debe tener dueño, especie y raza)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Mascota (
                IdMascota INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                IdUsuario INTEGER NOT NULL,
                Nombres TEXT NOT NULL,
                FechaNacimiento DATE,
                IdEspecie INTEGER NOT NULL,
                IdRaza INTEGER,
                Sexo TEXT,
                PesoActual DECIMAL,
                EsEsterilizado BOOLEAN,
                TieneChip BOOLEAN,
                Notas TEXT,
                Activo BOOLEAN DEFAULT TRUE,

                CONSTRAINT fk_mascota_usuario
                    FOREIGN KEY (IdUsuario)
                    REFERENCES Usuario(IdUsuario)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE,

                CONSTRAINT fk_mascota_especie
                    FOREIGN KEY (IdEspecie)
                    REFERENCES Especie(IdEspecie)
                    ON DELETE RESTRICT
                    ON UPDATE CASCADE,

                CONSTRAINT fk_mascota_raza
                    FOREIGN KEY (IdRaza)
                    REFERENCES Raza(IdRaza)
                    ON DELETE SET NULL
                    ON UPDATE CASCADE
            )
        """.trimIndent())

        // Tabla: TipoRecordatorio
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS TipoRecordatorio (
                IdTipoRecordatorio INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                Nombre TEXT NOT NULL UNIQUE,
                EsMedico BOOLEAN DEFAULT FALSE
            )
        """.trimIndent())

        // Tabla: Recordatorio (depende de un tipo de recordatorio,
        // está relacionado a una mascota)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Recordatorio (
                IdRecordatorio INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                IdUsuario INTEGER NOT NULL,
                IdMascota INTEGER NOT NULL,
                Titulo TEXT NOT NULL,
                Descripcion TEXT,
                IdTipoRecordatorio INTEGER NOT NULL,
                FechaInicio DATE NOT NULL,
                FechaFin DATE,
                SeRepite BOOLEAN DEFAULT FALSE,
                Frecuencia TEXT,
                FechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                Activo BOOLEAN DEFAULT TRUE,

                CONSTRAINT fk_recordatorio_usuario
                    FOREIGN KEY (IdUsuario)
                    REFERENCES Usuario(IdUsuario)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE,

                CONSTRAINT fk_recordatorio_mascota
                    FOREIGN KEY (IdMascota)
                    REFERENCES Mascota(IdMascota)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE,

                CONSTRAINT fk_recordatorio_tipo
                    FOREIGN KEY (IdTipoRecordatorio)
                    REFERENCES TipoRecordatorio(IdTipoRecordatorio)
                    ON DELETE RESTRICT
                    ON UPDATE CASCADE
            )
        """.trimIndent())

        // Tabla: HistorialRecordatorio
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Historial_Recordatorio (
                IdHistorial INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                IdRecordatorio INTEGER NOT NULL,
                FechaInicio DATE NOT NULL,
                FechaFin DATE,
                FechaCompletado DATE,
                Notas TEXT,
                Estado TEXT,
                Costo DECIMAL,

                CONSTRAINT fk_historial_recordatorio
                    FOREIGN KEY (IdRecordatorio)
                    REFERENCES Recordatorio(IdRecordatorio)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Actualiza la versión 1 a la 2 si es necesario
            db.execSQL("DROP TABLE IF EXISTS Usuario")
            db.execSQL("DROP TABLE IF EXISTS Especie")
            db.execSQL("DROP TABLE IF EXISTS Raza")
            db.execSQL("DROP TABLE IF EXISTS Mascota")
            db.execSQL("DROP TABLE IF EXISTS TipoRecordatorio")
            db.execSQL("DROP TABLE IF EXISTS Recordatorio")
            db.execSQL("DROP TABLE IF EXISTS Historial_Recordatorio")
            onCreate(db)
        }
    }
}