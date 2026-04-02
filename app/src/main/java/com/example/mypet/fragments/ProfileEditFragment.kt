package com.example.mypet.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mypet.R
import com.example.mypet.entity.firestore.UsuarioFirestore
import com.example.mypet.firebase.AuthHelper
import com.example.mypet.firebase.FirestoreHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class ProfileEditFragment : Fragment(R.layout.fragment_profile_edit) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSave = view.findViewById<MaterialButton>(R.id.btnProfileEditEditSave)
        val etFecha = view.findViewById<TextInputEditText>(R.id.etFecha)
        val actvGender = view.findViewById<AutoCompleteTextView>(R.id.actvGender)
        val etProfileEditName = view.findViewById<TextInputEditText>(R.id.etProfileEditName)
        val etProfileEditLastname = view.findViewById<TextInputEditText>(R.id.etProfileEditLastname)
        val etProfileEditEmail = view.findViewById<TextInputEditText>(R.id.etProfileEditEmail)
        val etProfileEditPhone = view.findViewById<TextInputEditText>(R.id.etProfileEditPhone)

        val currentUser = AuthHelper.auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        val firebaseUid = currentUser.uid

        FirestoreHelper.db.collection("usuarios")
            .document(firebaseUid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(UsuarioFirestore::class.java)

                    if (usuario != null) {
                        etProfileEditName.setText(usuario.nombres)
                        etProfileEditLastname.setText("${usuario.apellidoPaterno} ${usuario.apellidoMaterno}")
                        etFecha.setText(usuario.fechaNacimiento)
                        actvGender.setText(usuario.pronombre, false)
                        etProfileEditEmail.setText(usuario.email)
                        etProfileEditPhone.setText(usuario.telefono)
                    } else {
                        Toast.makeText(requireContext(), "No se pudo cargar el usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error al cargar los datos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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
            val nombres = etProfileEditName.text.toString().trim()
            val apellidosTexto = etProfileEditLastname.text.toString().trim()
            val email = etProfileEditEmail.text.toString().trim()
            val telefono = etProfileEditPhone.text.toString().trim()
            val fechaNacimiento = etFecha.text.toString().trim()
            val pronombre = actvGender.text.toString().trim()

            if (nombres.isEmpty() || apellidosTexto.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val partesApellidos = apellidosTexto.split("\\s+".toRegex())

            if (partesApellidos.size < 2) {
                Toast.makeText(
                    requireContext(),
                    "Ingresa apellido paterno y materno",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val apeP = partesApellidos[0]
            val apeM = partesApellidos.drop(1).joinToString(" ")

            val dataActualizada = mapOf(
                "nombres" to nombres,
                "apellidoPaterno" to apeP,
                "apellidoMaterno" to apeM,
                "email" to email,
                "telefono" to telefono,
                "fechaNacimiento" to fechaNacimiento,
                "pronombre" to pronombre,
                "activo" to true
            )

            FirestoreHelper.db.collection("usuarios")
                .document(firebaseUid)
                .update(dataActualizada)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentProfileContainer, ProfileFragment())
                        .commit()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Error al actualizar: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}