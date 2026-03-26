package com.example.mypet

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
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
import com.example.mypet.dao.MascotaDAO
import com.example.mypet.dao.RazaDAO
import com.example.mypet.entity.Mascota
import com.example.mypet.entity.Raza
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class RegisterPetActivity : AppCompatActivity() {
    val mascotaDAO = MascotaDAO(this)
    val especieDAO = EspecieDAO(this)
    val razaDAO = RazaDAO(this)

    lateinit var listaRazasActual: List<Raza>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_pet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegistrar = findViewById<Button>(R.id.btnRegisterPet)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val petNeuteredDateEditText = findViewById<TextInputEditText>(R.id.petNeuteredDateEditText)

        val etNombre = findViewById<TextInputEditText>(R.id.petNameEditText)

        val spEspecie = findViewById<Spinner>(R.id.petSpeciesSpinner)
        val listaEspecies = especieDAO.obtenerEspecies()
        val nombresEspecie = listaEspecies.map { it.nombreEspecie }
        val adapterEspecie = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombresEspecie
        )
        adapterEspecie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEspecie.adapter = adapterEspecie

        val spRaza = findViewById<Spinner>(R.id.petBreedSpinner)
//        val listaRazas = razaDAO.obtenerRazaPorIdEspecie(especie)
//        val nombresRazas = listaRazas.map { it.nombreRaza }
//        val adapterRaza = ArrayAdapter(
//            this,
//            android.R.layout.simple_spinner_item,
//            nombresRazas
//        )
//        adapterRaza.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spRaza.adapter = adapterRaza

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

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val etPeso = findViewById<TextInputEditText>(R.id.petWeightEditText)
        val etMicrochip = findViewById<MaterialCheckBox>(R.id.petMicrochipCheckBox)

        val cbEsterilizado = findViewById<MaterialCheckBox>(R.id.petNeuteredCheckBox)

        val etVacunas = findViewById<TextInputEditText>(R.id.petVaccinesEditText)
        val etAlergias = findViewById<TextInputEditText>(R.id.petAllergiesEditText)
        val etEnfermedades = findViewById<TextInputEditText>(R.id.petDiseasesEditText)
        val etBanhos = findViewById<TextInputEditText>(R.id.petBathsEditText)
        val etJuguete = findViewById<TextInputEditText>(R.id.petToysEditText)
        val etComida = findViewById<TextInputEditText>(R.id.petFoodEditText)
        

        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etFecha.setText(date)
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        petNeuteredDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    petNeuteredDateEditText.setText(date)
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        btnRegistrar.setOnClickListener {

            val nombre = etNombre.text.toString()
            val fecha = etFecha.text.toString()
            val peso = etPeso.text.toString()

            // Validación básica
            if (nombre.isEmpty()) {
                etNombre.error = "Ingrese nombre"
                return@setOnClickListener
            }

            val especieSeleccionada = listaEspecies[spEspecie.selectedItemPosition]
            val especie = especieSeleccionada.idEspecie

            val razaSeleccionada = listaRazasActual[spRaza.selectedItemPosition]
            val raza = razaSeleccionada.idRaza
            
            val mascota = Mascota(
                idMascota = 0,
                idUsuario = 1,
                nombres = nombre,
                fechaNacimiento = fecha,
                idEspecie = especie,
                idRaza = raza,
                sexo = "M",
                pesoActual = peso,
                esEsterilizado = cbEsterilizado.isChecked,
                tieneChip = etMicrochip.isChecked,
                notas = """
            Vacunas: ${etVacunas.text}
            Alergias: ${etAlergias.text}
            Enfermedades: ${etEnfermedades.text}
            Frecuencia de Baños: ${etBanhos.text}
            Juguete Favorito: ${etJuguete}
            Comida Favorita: ${etComida}
        """.trimIndent(),
                activo = true
            )

            val resultado = mascotaDAO.insert(mascota)

            if (resultado > 0) {
                finish()
            } else {
                Toast.makeText(this, "Error al registrar la mascota", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }

    }
}