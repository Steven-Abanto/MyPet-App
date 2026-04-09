package com.example.mypet

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.adapter.ReminderAdapter
import com.example.mypet.dao.MascotaDAO
import com.example.mypet.dao.RecordatorioDAO
import com.example.mypet.dao.TipoRecordatorioDAO
import com.example.mypet.dao.UsuarioDAO
import com.example.mypet.entity.Recordatorio
import com.example.mypet.entity.firestore.RecordatorioFirestore
import com.example.mypet.entity.mappers.RecordatorioDetalle
import com.example.mypet.firebase.AuthHelper
import com.example.mypet.repository.RecordatorioFirestoreRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.util.Calendar
import java.util.Locale

class ReminderActivity : AppCompatActivity() {

    private lateinit var rvReminders: RecyclerView
    private lateinit var reminderAdapter: ReminderAdapter

    private lateinit var recordatorioDAO: RecordatorioDAO
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var mascotaDAO: MascotaDAO
    private lateinit var tipoDAO: TipoRecordatorioDAO
    private lateinit var repo: RecordatorioFirestoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reminder)

        recordatorioDAO = RecordatorioDAO(this)
        usuarioDAO = UsuarioDAO(this)
        mascotaDAO = MascotaDAO(this)
        tipoDAO = TipoRecordatorioDAO(this)
        repo = RecordatorioFirestoreRepository(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecycler()
        setupBottomNav()

        findViewById<ImageView>(R.id.ivAddReminder).setOnClickListener {
            abrirModal()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLocal()
        sincronizar()
    }

    private fun setupRecycler() {
        rvReminders = findViewById(R.id.rvReminders)
        rvReminders.layoutManager = LinearLayoutManager(this)

        reminderAdapter = ReminderAdapter(emptyList()) { recordatorio ->
            mostrarOpciones(recordatorio)
        }

        rvReminders.adapter = reminderAdapter
    }

    private fun cargarLocal() {
        val usuario = obtenerUsuarioActualLocal() ?: return

        val lista = recordatorioDAO.obtenerRecordatoriosPorUsuario(usuario.idUsuario)

        reminderAdapter = ReminderAdapter(lista) { recordatorio ->
            mostrarOpciones(recordatorio)
        }

        rvReminders.adapter = reminderAdapter
    }

    private fun sincronizar() {
        val uid = AuthHelper.auth.currentUser?.uid

        if (uid.isNullOrEmpty()) {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        repo.sincronizarRecordatoriosDeUsuarioALocal(uid) { ok, error ->
            runOnUiThread {
                if (ok) {
                    cargarLocal()
                } else {
                    Toast.makeText(
                        this,
                        error ?: "No se pudo sincronizar recordatorios",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun abrirModal() {
        val usuario = obtenerUsuarioActualLocal()
        if (usuario == null) {
            Toast.makeText(this, "No se encontró el usuario local", Toast.LENGTH_SHORT).show()
            return
        }

        val mascotas = mascotaDAO.obtenerMascotaPorIdUsuario(usuario.idUsuario)
        if (mascotas.isEmpty()) {
            Toast.makeText(this, "Primero debes registrar una mascota", Toast.LENGTH_SHORT).show()
            return
        }

        val tipos = tipoDAO.obtenerTodosConCategoria()
        if (tipos.isEmpty()) {
            Toast.makeText(this, "No hay tipos de recordatorio disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val view = layoutInflater.inflate(R.layout.modal_add_reminder, null)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        val etTitulo = view.findViewById<EditText>(R.id.etTituloReminder)
        val etFecha = view.findViewById<EditText>(R.id.etFechaInicioReminder)
        val etDescripcion = view.findViewById<EditText>(R.id.etDescripcionReminder)

        val spMascota = view.findViewById<Spinner>(R.id.spMascotaReminder)
        val spTipo = view.findViewById<Spinner>(R.id.spTipoReminder)
        val spFrecuencia = view.findViewById<Spinner>(R.id.spFrecuenciaReminder)

        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarReminder)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarReminder)

        val mascotaItems = mascotas.map {
            MascotaSpinnerItem(
                idMascota = it.idMascota,
                firestoreId = it.firestoreId ?: "",
                nombre = it.nombres
            )
        }

        val tipoItems = tipos.map {
            TipoSpinnerItem(
                idTipoRecordatorio = it.idTipoRecordatorio,
                nombreTipo = it.nombreTipo,
                nombreCategoria = it.nombreCategoria
            )
        }

        val frecuencias = listOf("UNA_VEZ", "DIARIO", "SEMANAL", "MENSUAL", "ANUAL")

        spMascota.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            mascotaItems.map { it.nombre }
        )

        spTipo.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            tipoItems.map { "${it.nombreTipo} (${it.nombreCategoria})" }
        )

        spFrecuencia.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            frecuencias
        )

        etFecha.setOnClickListener {
            mostrarDatePicker(etFecha)
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val fecha = etFecha.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim().ifEmpty { null }

            if (titulo.isEmpty()) {
                etTitulo.error = "Ingrese un título"
                etTitulo.requestFocus()
                return@setOnClickListener
            }

            if (fecha.isEmpty()) {
                etFecha.error = "Seleccione una fecha"
                etFecha.requestFocus()
                return@setOnClickListener
            }

            val uid = AuthHelper.auth.currentUser?.uid
            if (uid.isNullOrEmpty()) {
                Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mascotaSeleccionada = mascotaItems[spMascota.selectedItemPosition]
            val tipoSeleccionado = tipoItems[spTipo.selectedItemPosition]
            val frecuenciaSeleccionada = frecuencias[spFrecuencia.selectedItemPosition]

            if (mascotaSeleccionada.firestoreId.isEmpty()) {
                Toast.makeText(
                    this,
                    "La mascota seleccionada aún no tiene FirestoreId",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val recordatorioLocal = Recordatorio(
                idMascota = mascotaSeleccionada.idMascota,
                titulo = titulo,
                descripcion = descripcion,
                idTipoRecordatorio = tipoSeleccionado.idTipoRecordatorio,
                fechaInicio = fecha,
                fechaFin = null,
                frecuencia = frecuenciaSeleccionada,
                activo = true
            )

            val recordatorioFirestore = RecordatorioFirestore(
                uidUsuario = uid,
                mascotaFirestoreId = mascotaSeleccionada.firestoreId,
                titulo = titulo,
                descripcion = descripcion,
                tipoNombre = tipoSeleccionado.nombreTipo,
                categoriaNombre = tipoSeleccionado.nombreCategoria,
                fechaInicio = fecha,
                fechaFin = null,
                frecuencia = frecuenciaSeleccionada,
                activo = true
            )

            repo.registrarRecordatorio(
                recordatorioFirestore = recordatorioFirestore,
                recordatorioLocalBase = recordatorioLocal
            ) { ok, error ->
                runOnUiThread {
                    if (ok) {
                        Toast.makeText(this, "Recordatorio guardado", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        cargarLocal()
                    } else {
                        Toast.makeText(
                            this,
                            error ?: "No se pudo guardar el recordatorio",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun mostrarDatePicker(target: EditText) {
        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val fecha = String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    dayOfMonth
                )
                target.setText(fecha)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun mostrarOpciones(recordatorio: RecordatorioDetalle) {
        val view = layoutInflater.inflate(R.layout.modal_options, null)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        val tvTitulo = view.findViewById<TextView>(R.id.tvTitulo)
        val btnEliminar = view.findViewById<MaterialButton>(R.id.mbEliminar)

        tvTitulo.text = "Opciones para ${recordatorio.titulo}"

        btnEliminar.setOnClickListener {
            val filas = recordatorioDAO.desactivar(recordatorio.idRecordatorio)

            if (filas > 0) {
                Toast.makeText(this, "Recordatorio desactivado", Toast.LENGTH_SHORT).show()
                cargarLocal()
            } else {
                Toast.makeText(this, "No se pudo desactivar el recordatorio", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun obtenerUsuarioActualLocal() =
        AuthHelper.auth.currentUser?.uid?.let { usuarioDAO.obtenerPorFirebaseUid(it) }

    private fun setupBottomNav() {
        val nav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        nav.selectedItemId = R.id.navigation_memo

        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_memo -> true
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    data class MascotaSpinnerItem(
        val idMascota: Int,
        val firestoreId: String,
        val nombre: String
    )

    data class TipoSpinnerItem(
        val idTipoRecordatorio: Int,
        val nombreTipo: String,
        val nombreCategoria: String
    )
}