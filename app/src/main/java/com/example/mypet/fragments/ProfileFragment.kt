package com.example.mypet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.mypet.R
import com.example.mypet.dao.UsuarioDAO // Asegúrate de que la ruta sea correcta
import com.google.android.material.button.MaterialButton

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var usuarioDAO: UsuarioDAO

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos el DAO
        usuarioDAO = UsuarioDAO(requireContext())

        // Campos del fragment
        val tvNombre = view.findViewById<TextView>(R.id.tvProfileNameText)
        val tvApellidos = view.findViewById<TextView>(R.id.tvProfileLastnameText)
        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmailText)
        val tvTelefono = view.findViewById<TextView>(R.id.tvProfilePhoneText)
        val tvFechaNacimiento = view.findViewById<TextView>(R.id.tvProfileBirthdateText)
        val tvPronombre = view.findViewById<TextView>(R.id.tvProfileGenderText)

        val btnEdit = view.findViewById<MaterialButton>(R.id.btnProfileEdit)

        // Seteamos id de prueba para cargar datos
        val idUsuarioLogueado = 1
        val usuario = usuarioDAO.obtenerUsuarioPorId(idUsuarioLogueado)[0]

        // Se cargan datos
        if (usuario != null) {
            tvNombre.text = usuario.nombres
            tvApellidos.text = "${usuario.apellidoPaterno} ${usuario.apellidoMaterno}"
            tvFechaNacimiento.text = usuario.fechaNacimiento
            tvPronombre.text = usuario.pronombre
            tvEmail.text = usuario.email
            tvTelefono.text = usuario.telefono
        } else {
            Toast.makeText(requireContext(), "No se pudo cargar la información", Toast.LENGTH_SHORT).show()
        }

        btnEdit.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentProfileContainer, ProfileEditFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}