package com.example.mypet

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mypet.dao.EspecieDAO
import com.example.mypet.dao.RazaDAO
import com.example.mypet.dao.UsuarioDAO
import com.example.mypet.entity.Mascota
import com.example.mypet.entity.Raza
import com.example.mypet.entity.firestore.MascotaFirestore
import com.example.mypet.firebase.AuthHelper
import com.example.mypet.repository.MascotaFirestoreRepository
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class RegisterPetActivity : AppCompatActivity() {

    private val especieDAO by lazy { EspecieDAO(this) }
    private val razaDAO by lazy { RazaDAO(this) }
    private val usuarioDAO by lazy { UsuarioDAO(this) }
    private val mascotaRepo by lazy { MascotaFirestoreRepository(this) }

    private var listaRazasActual: List<Raza> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_pet)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val firebaseUid = AuthHelper.auth.currentUser?.uid
        if (firebaseUid.isNullOrEmpty()) {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val usuarioLocal = usuarioDAO.obtenerPorFirebaseUid(firebaseUid)
        if (usuarioLocal == null) {
            Toast.makeText(this, "No se encontró el usuario en la base local", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val idUsuarioLocal = usuarioLocal.idUsuario

        val btnRegistrar = findViewById<Button>(R.id.btnRegisterPet)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val etFechaEsterilizacion = findViewById<TextInputEditText>(R.id.petNeuteredDateEditText)

        val etNombre = findViewById<TextInputEditText>(R.id.petNameEditText)
        val etPeso = findViewById<TextInputEditText>(R.id.petWeightEditText)
        val cbMicrochip = findViewById<MaterialCheckBox>(R.id.petMicrochipCheckBox)
        val cbEsterilizado = findViewById<MaterialCheckBox>(R.id.petNeuteredCheckBox)
        val etVacunas = findViewById<TextInputEditText>(R.id.petVaccinesEditText)
        val etAlergias = findViewById<TextInputEditText>(R.id.petAllergiesEditText)
        val etEnfermedades = findViewById<TextInputEditText>(R.id.petDiseasesEditText)
        val etBanhos = findViewById<TextInputEditText>(R.id.petBathsEditText)
        val etJuguete = findViewById<TextInputEditText>(R.id.petToysEditText)
        val etComida = findViewById<TextInputEditText>(R.id.petFoodEditText)

        val spEspecie = findViewById<Spinner>(R.id.petSpeciesSpinner)
        val spRaza = findViewById<Spinner>(R.id.petBreedSpinner)

        val listaEspecies = especieDAO.obtenerEspecies()
        if (listaEspecies.isEmpty()) {
            Toast.makeText(this, "No hay especies registradas en la base local", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val nombresEspecie = listaEspecies.map { it.nombreEspecie }
        val adapterEspecie = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombresEspecie
        )
        adapterEspecie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEspecie.adapter = adapterEspecie

        spEspecie.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val especieSeleccionada = listaEspecies[position]
                val idEspecie = especieSeleccionada.idEspecie

                listaRazasActual = razaDAO.obtenerRazaPorIdEspecie(idEspecie)
                val nombresRazas = listaRazasActual.map { it.nombreRaza }

                val adapterRaza = ArrayAdapter(
                    this@RegisterPetActivity,
                    android.R.layout.simple_spinner_item,
                    nombresRazas
                )
                adapterRaza.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spRaza.adapter = adapterRaza
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                listaRazasActual = emptyList()
                spRaza.adapter = null
            }
        }

        etFecha.setOnClickListener { mostrarDatePicker(etFecha) }
        etFechaEsterilizacion.setOnClickListener { mostrarDatePicker(etFechaEsterilizacion) }

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text?.toString()?.trim().orEmpty()
            val fecha = etFecha.text?.toString()?.trim().orEmpty()
            val peso = etPeso.text?.toString()?.trim().orEmpty()

            if (nombre.isEmpty()) {
                etNombre.error = "Ingrese nombre"
                etNombre.requestFocus()
                return@setOnClickListener
            }

            if (spEspecie.selectedItemPosition == AdapterView.INVALID_POSITION) {
                Toast.makeText(this, "Seleccione una especie", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (listaRazasActual.isEmpty() || spRaza.selectedItemPosition == AdapterView.INVALID_POSITION) {
                Toast.makeText(this, "Seleccione una raza válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val especieSeleccionada = listaEspecies[spEspecie.selectedItemPosition]
            val razaSeleccionada = listaRazasActual[spRaza.selectedItemPosition]

            val notas = """
                Vacunas: ${etVacunas.text?.toString()?.trim().orEmpty()}
                Alergias: ${etAlergias.text?.toString()?.trim().orEmpty()}
                Enfermedades: ${etEnfermedades.text?.toString()?.trim().orEmpty()}
                Frecuencia de Baños: ${etBanhos.text?.toString()?.trim().orEmpty()}
                Juguete Favorito: ${etJuguete.text?.toString()?.trim().orEmpty()}
                Comida Favorita: ${etComida.text?.toString()?.trim().orEmpty()}
                Fecha de esterilización: ${etFechaEsterilizacion.text?.toString()?.trim().orEmpty()}
            """.trimIndent()

            // Como no tienes campo Sexo en la UI, se guarda un valor fijo temporal.
            val sexoTemporal = "M"

            val mascotaFirestore = MascotaFirestore(
                firebaseUid = firebaseUid,
                nombres = nombre,
                fechaNacimiento = fecha,
                idEspecie = especieSeleccionada.idEspecie,
                idRaza = razaSeleccionada.idRaza,
                sexo = sexoTemporal,
                pesoActual = peso,
                esEsterilizado = cbEsterilizado.isChecked,
                tieneChip = cbMicrochip.isChecked,
                notas = notas,
                activo = true
            )

            val mascotaLocal = Mascota(
                idMascota = 0,
                firestoreId = null,
                idUsuario = idUsuarioLocal,
                nombres = nombre,
                fechaNacimiento = fecha,
                idEspecie = especieSeleccionada.idEspecie,
                idRaza = razaSeleccionada.idRaza,
                sexo = sexoTemporal,
                pesoActual = peso,
                esEsterilizado = cbEsterilizado.isChecked,
                tieneChip = cbMicrochip.isChecked,
                notas = notas,
                activo = true
            )

            btnRegistrar.isEnabled = false

            android.util.Log.d("PET_DEBUG", "firebaseUid=$firebaseUid")
            android.util.Log.d("PET_DEBUG", "idUsuarioLocal=$idUsuarioLocal")
            android.util.Log.d("PET_DEBUG", "mascotaLocal.idUsuario=${mascotaLocal.idUsuario}")

            mascotaRepo.registrarMascota(
                idUsuarioLocal = idUsuarioLocal,
                mascotaFirestore = mascotaFirestore,
                mascotaLocalBase = mascotaLocal
            ) { ok, error ->
                runOnUiThread {
                    btnRegistrar.isEnabled = true

                    if (ok) {
                        Toast.makeText(this, "Mascota registrada correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Error al registrar la mascota: $error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun mostrarDatePicker(target: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                target.setText(date)
            },
            year,
            month,
            day
        )
        datePicker.show()
    }
}