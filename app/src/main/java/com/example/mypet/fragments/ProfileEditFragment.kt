package com.example.mypet.fragments

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.idatdemo.data.AppDatabaseHelper
import com.example.mypet.R
import com.example.mypet.dao.UsuarioDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class ProfileEditFragment : Fragment(R.layout.fragment_profile_edit) {
    private lateinit var usuarioDAO: UsuarioDAO

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnSave = view.findViewById<MaterialButton>(R.id.btnProfileEditEditSave)
        val etFecha = view.findViewById<TextInputEditText>(R.id.etFecha)
        val actvGender = view.findViewById<AutoCompleteTextView>(R.id.actvGender)
        val etProfileEditName = view.findViewById<TextInputEditText>(R.id.etProfileEditName)
        val etProfileEditLastname = view.findViewById<TextInputEditText>(R.id.etProfileEditLastname)
        val etProfileEditEmail = view.findViewById<TextInputEditText>(R.id.etProfileEditEmail)
        val etProfileEditPhone = view.findViewById<TextInputEditText>(R.id.etProfileEditPhone)

        // Inicializamos el DAO
        usuarioDAO = UsuarioDAO(requireContext())
        // Seteamos id de prueba para cargar datos
        val idUsuarioLogueado = 1
        val usuario = usuarioDAO.obtenerUsuarioPorId(idUsuarioLogueado)[0]

        // Se cargan datos
        if (usuario != null) {
            etProfileEditName.setText(usuario.nombres)
            etProfileEditLastname.setText("${usuario.apellidoPaterno} ${usuario.apellidoMaterno}")
            etFecha.setText(usuario.fechaNacimiento)
            actvGender.setText(usuario.pronombre)
            etProfileEditEmail.setText(usuario.email)
            etProfileEditPhone.setText(usuario.telefono)
        } else {
            Toast.makeText(requireContext(), "No se pudo cargar la información", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.gender_options)
        )

        actvGender.setAdapter(adapter)
        actvGender.setOnClickListener {
            actvGender.showDropDown()
        }

        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
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

        btnSave.setOnClickListener {
//            parentFragmentManager.popBackStack()
            val dbHelper = AppDatabaseHelper(requireContext())
            val db = dbHelper.writableDatabase
            val valoresUsuario = ContentValues().apply {
                put("Nombres",  etProfileEditName.text.toString().trim())
                put("ApellidoPaterno", etProfileEditLastname.text.toString().trimStart())
                put("ApellidoMaterno", etProfileEditLastname.text.toString().trimEnd())
                put("Pronombre", actvGender.text.toString())
                put("FechaNacimiento", etFecha.text.toString())
                put("Email", etProfileEditEmail.text.toString().trim())
                put("Telefono", etProfileEditPhone.text.toString())
                put("ContrasenaHashed", usuario.contrasenaHashed)
                put("FechaCreacion", usuario.fechaCreacion)
                put("Activo", true)
            }

//            val idUsuario = db.insert("Usuario", null, valoresUsuario)

            val filasAfectadas = db.update("Usuario",
                valoresUsuario,
                "idUsuario = ?",
                arrayOf(usuario.idUsuario.toString())
            )

            if (filasAfectadas > 0) {
                Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error al registrar", Toast.LENGTH_SHORT).show()
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentProfileContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}