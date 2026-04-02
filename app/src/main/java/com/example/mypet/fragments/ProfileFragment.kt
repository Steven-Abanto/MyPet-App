package com.example.mypet.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.entity.firestore.UsuarioFirestore
import com.example.mypet.firebase.AuthHelper
import com.example.mypet.firebase.FirestoreHelper
import com.google.android.material.button.MaterialButton

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNombre = view.findViewById<TextView>(R.id.tvProfileNameText)
        val tvApellidos = view.findViewById<TextView>(R.id.tvProfileLastnameText)
        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmailText)
        val tvTelefono = view.findViewById<TextView>(R.id.tvProfilePhoneText)
        val tvFechaNacimiento = view.findViewById<TextView>(R.id.tvProfileBirthdateText)
        val tvPronombre = view.findViewById<TextView>(R.id.tvProfileGenderText)

        val btnEdit = view.findViewById<MaterialButton>(R.id.btnProfileEdit)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)

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
                        tvNombre.text = usuario.nombres
                        tvApellidos.text = "${usuario.apellidoPaterno} ${usuario.apellidoMaterno}"
                        tvEmail.text = usuario.email
                        tvTelefono.text = if (usuario.telefono.isNotEmpty()) usuario.telefono else "No registrado"
                        tvFechaNacimiento.text = if (usuario.fechaNacimiento.isNotEmpty()) usuario.fechaNacimiento else "No registrada"
                        tvPronombre.text = if (usuario.pronombre.isNotEmpty()) usuario.pronombre else "No especificado"
                    } else {
                        Toast.makeText(requireContext(), "No se pudo convertir la información del usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error al cargar el perfil: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        btnEdit.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentProfileContainer, ProfileEditFragment())
                .addToBackStack(null)
                .commit()
        }

        btnLogout.setOnClickListener {
            AuthHelper.auth.signOut()

            val sharedPreferences = requireActivity()
                .getSharedPreferences("mypet_session", android.content.Context.MODE_PRIVATE)

            sharedPreferences.edit().clear().apply()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            requireActivity().finish()
        }
    }
}