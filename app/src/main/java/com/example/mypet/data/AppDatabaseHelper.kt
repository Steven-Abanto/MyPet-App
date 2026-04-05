package com.example.idatdemo.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context : Context) : SQLiteOpenHelper(context, "mypet.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = ON")

        // Tabla: Usuario
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Usuario (
                IdUsuario INTEGER PRIMARY KEY AUTOINCREMENT,
                FirebaseUid TEXT UNIQUE,
                Nombres TEXT,
                ApellidoPaterno TEXT,
                ApellidoMaterno TEXT,
                Email TEXT,
                Telefono TEXT,
                FechaNacimiento TEXT,
                Pronombre TEXT,
                Activo INTEGER DEFAULT 1
            )
        """.trimIndent())

        // Tabla: Especie
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Especie (
                IdEspecie INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                NombreEspecie TEXT NOT NULL UNIQUE
            )
        """.trimIndent())

        db.execSQL("INSERT INTO Especie (NombreEspecie) VALUES ('Perro')")
        db.execSQL("INSERT INTO Especie (NombreEspecie) VALUES ('Gato')")
        db.execSQL("INSERT INTO Especie (NombreEspecie) VALUES ('Ave')")
        db.execSQL("INSERT INTO Especie (NombreEspecie) VALUES ('Conejo')")

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

        // Razas de perro
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Perro'), 'Labrador Retriever')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Perro'), 'Bulldog')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Perro'), 'Pastor Alemán')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Perro'), 'Golden Retriever')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Perro'), 'Poodle')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Perro'), 'Chihuahua')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Perro'), 'Rottweiler')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Perro'), 'Mixto')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Perro'), 'No especificado')")

        // Razas de gato
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Gato'), 'Persa')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Gato'), 'Siamés')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Gato'), 'Maine Coon')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Gato'), 'Bengalí')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Gato'), 'Esfinge')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Gato'), 'Mixto')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Gato'), 'No especificado')")

        // Razas de ave
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Ave'), 'Periquito')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Ave'), 'Canario')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Ave'), 'Loro')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Ave'), 'Cacatúa')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Ave'), 'Mixto')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Ave'), 'No especificado')")

        // Razas de conejo
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Conejo'), 'Cabeza de León')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Conejo'), 'Rex')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Conejo'), 'Enano Holandés')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Conejo'), 'Mixto')")
        db.execSQL("INSERT INTO Raza (IdEspecie, NombreRaza) VALUES ((SELECT IdEspecie FROM Especie WHERE NombreEspecie = 'Conejo'), 'No especificado')")

        // Tabla: Mascota (Debe tener dueño, especie y raza)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Mascota (
                IdMascota INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                FirestoreId TEXT UNIQUE,
                IdUsuario INTEGER NOT NULL,
                Nombres TEXT NOT NULL,
                FechaNacimiento DATE,
                IdEspecie TEXT NOT NULL,
                IdRaza TEXT NOT NULL,
                Sexo TEXT,
                PesoActual DECIMAL(5,2),
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
                    ON DELETE RESTRICT
                    ON UPDATE CASCADE
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS CategoriaRecordatorio (
                IdCategoriaRecordatorio INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                Nombre TEXT NOT NULL UNIQUE /*Médico, Cuidados Generales, Rutina, etc*/
            )
        """.trimIndent())

        db.execSQL("""
            INSERT INTO CategoriaRecordatorio (Nombre) VALUES
                ('Personalizado'), /*Siempre debe ser id 1 para evitar problemas*/
                ('Médico'),
                ('Cuidados generales'),
                ('Rutina'),
                ('Otro');
        """.trimIndent())

        // Tabla: TipoRecordatorio
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS TipoRecordatorio (
                IdTipoRecordatorio INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                IdCategoriaRecordatorio INTEGER NOT NULL,
                Nombre TEXT NOT NULL, /*Vacunación, Baño, Paseo, etc.*/
                                
                CONSTRAINT fk_tipo_categoria
                    FOREIGN KEY (IdCategoriaRecordatorio)
                    REFERENCES CategoriaRecordatorio(IdCategoriaRecordatorio)
                    ON DELETE RESTRICT
                    ON UPDATE CASCADE,
                    
                CONSTRAINT uq_tipo_categoria_nombre
                    UNIQUE (IdCategoriaRecordatorio, Nombre)
            )
        """.trimIndent())

        db.execSQL("""
            INSERT INTO TipoRecordatorio (IdCategoriaRecordatorio, Nombre) VALUES
                (2, 'Vacunación'),
                (2, 'Desparasitación'),
                (2, 'Consulta médica'),
                (2, 'Medicación'),
                (2, 'Control de peso'),
    
                (3, 'Baño'),
                (3, 'Corte de uñas'),
                (3, 'Peluquería'),
                (3, 'Limpieza dental'),
    
                (4, 'Paseo'),
                (4, 'Alimentación especial'),
                (4, 'Ejercicio'),
                (4, 'Entrenamiento'),
    
                (5, 'Otro');
        """.trimIndent())

        // Tabla: Recordatorio (depende de un tipo de recordatorio,
        // está relacionado a una mascota)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Recordatorio (
                IdRecordatorio INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                FirestoreId TEXT,
                IdMascota INTEGER NOT NULL,
                Titulo TEXT NOT NULL,
                Descripcion TEXT,
                IdTipoRecordatorio INTEGER NOT NULL,
                FechaInicio DATE NOT NULL,
                FechaFin DATE,
                Frecuencia TEXT NOT NULL CHECK(Frecuencia IN ('UNA_VEZ','DIARIO','SEMANAL','MENSUAL','ANUAL')),
                FechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                UltimaModificacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                Activo BOOLEAN DEFAULT TRUE,

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

        // Tabla: Historial_Recordatorio
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Historial_Recordatorio (
                IdHistorial INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                FirestoreId TEXT,
                IdRecordatorio INTEGER NOT NULL,
                FechaProgramada DATE NOT NULL,
                FechaCompletado TEXT,
                Notas TEXT,
                Estado TEXT NOT NULL CHECK(Estado IN ('PENDIENTE', 'COMPLETADO', 'OMITIDO', 'VENCIDO', 'CANCELADO')),
                FechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                UltimaModificacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                Activo BOOLEAN DEFAULT TRUE,
            
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