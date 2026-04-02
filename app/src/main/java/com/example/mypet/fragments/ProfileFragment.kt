package com.example.mypet.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.entity.firestore.UsuarioFirestore
import com.example.mypet.firebase.AuthHelper
import com.example.mypet.firebase.FirestoreHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.EmailAuthProvider

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var ivProfilePic: ShapeableImageView
    private lateinit var lottieProfile: LottieAnimationView
    private lateinit var profilePicContainer: View

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                try {
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {
                }

                guardarUriFoto(uri.toString())
                mostrarFotoPerfil(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNombre = view.findViewById<TextView>(R.id.tvProfileNameText)
        val tvApellidos = view.findViewById<TextView>(R.id.tvProfileLastnameText)
        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmailText)
        val tvTelefono = view.findViewById<TextView>(R.id.tvProfilePhoneText)
        val tvFechaNacimiento = view.findViewById<TextView>(R.id.tvProfileBirthdateText)
        val tvPronombre = view.findViewById<TextView>(R.id.tvProfileGenderText)

        ivProfilePic = view.findViewById(R.id.ivProfilePic)
        lottieProfile = view.findViewById(R.id.lottieProfile)
        profilePicContainer = view.findViewById(R.id.profilePicContainer)

        val btnEdit = view.findViewById<MaterialButton>(R.id.btnProfileEdit)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogout)
        val btnPasswordEdit = view.findViewById<MaterialButton>(R.id.btnProfilePasswordEdit)

        val currentUser = AuthHelper.auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        val firebaseUid = currentUser.uid

        cargarFotoGuardada()

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

        profilePicContainer.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        btnPasswordEdit.setOnClickListener {
            val user = AuthHelper.auth.currentUser
            val currentPassword = "contraseña_actual"
            val newPassword = "nueva_contraseña"

            if (user == null || user.email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            user.reauthenticate(credential)
                .addOnSuccessListener {
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al actualizar contraseña: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Contraseña actual incorrecta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        AuthHelper.auth.signOut()

        val sharedPreferences = requireActivity()
            .getSharedPreferences("mypet_session", android.content.Context.MODE_PRIVATE)

        sharedPreferences.edit().clear().apply()

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish()
    }

    private fun mostrarFotoPerfil(uri: Uri) {
        ivProfilePic.setImageURI(uri)
        ivProfilePic.visibility = View.VISIBLE
        lottieProfile.visibility = View.GONE
    }

    private fun cargarFotoGuardada() {
        val prefs = requireContext().getSharedPreferences(
            "mypet_profile",
            android.content.Context.MODE_PRIVATE
        )
        val uriString = prefs.getString("profile_image_uri", null)

        if (!uriString.isNullOrEmpty()) {
            val uri = Uri.parse(uriString)
            mostrarFotoPerfil(uri)
        } else {
            ivProfilePic.visibility = View.GONE
            lottieProfile.visibility = View.VISIBLE
        }
    }

    private fun guardarUriFoto(uri: String) {
        val prefs = requireContext().getSharedPreferences("mypet_profile", android.content.Context.MODE_PRIVATE)
        prefs.edit().putString("profile_image_uri", uri).apply()
    }
}