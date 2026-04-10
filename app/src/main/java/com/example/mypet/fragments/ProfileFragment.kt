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
import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import com.example.mypet.dao.UsuarioDAO
import com.example.mypet.entity.Usuario
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var ivProfilePic: ShapeableImageView
    private lateinit var lottieProfile: LottieAnimationView
    private lateinit var profilePicContainer: View
    private lateinit var usuarioDAO: UsuarioDAO

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
                guardarImagenInterna(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuarioDAO = UsuarioDAO(requireContext())

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
        val btnEmailEdit = view.findViewById<MaterialButton>(R.id.btnProfileEmailEdit)

        val currentUser = AuthHelper.auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        val firebaseUid = currentUser.uid

        cargarFotoGuardada(firebaseUid)
        sincronizarEmailAuthConFirestore {
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

                            val usuarioLocal = Usuario(
                                idUsuario = 0,
                                firebaseUid = firebaseUid,
                                nombres = usuario.nombres,
                                apellidoPaterno = usuario.apellidoPaterno,
                                apellidoMaterno = usuario.apellidoMaterno,
                                email = usuario.email,
                                telefono = usuario.telefono,
                                fechaNacimiento = usuario.fechaNacimiento,
                                pronombre = usuario.pronombre,
                                activo = usuario.activo
                            )

                            usuarioDAO.guardarOActualizar(usuarioLocal)
                        } else {
                            Toast.makeText(requireContext(), "No se pudo convertir la información del usuario", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PROFILE_FIRESTORE", "Error al cargar desde Firestore", e)

                    val usuarioLocal = usuarioDAO.obtenerPorFirebaseUid(firebaseUid)

                    if (usuarioLocal != null) {
                        tvNombre.text = usuarioLocal.nombres
                        tvApellidos.text = "${usuarioLocal.apellidoPaterno} ${usuarioLocal.apellidoMaterno}"
                        tvEmail.text = usuarioLocal.email
                        tvTelefono.text = if (usuarioLocal.telefono.isNotEmpty()) usuarioLocal.telefono else "No registrado"
                        tvFechaNacimiento.text = if (usuarioLocal.fechaNacimiento.isNotEmpty()) usuarioLocal.fechaNacimiento else "No registrada"
                        tvPronombre.text = if (usuarioLocal.pronombre.isNotEmpty()) usuarioLocal.pronombre else "No especificado"

                        Toast.makeText(requireContext(), "Mostrando datos locales (modo offline)", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al cargar el perfil: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        btnEdit.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentProfileContainer, ProfileEditFragment())
                .addToBackStack(null)
                .commit()
        }

        profilePicContainer.setOnLongClickListener {
            val dialogView = layoutInflater.inflate(R.layout.modal_pic_options, null)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()
            dialog.show()

            val btnEliminar = dialogView.findViewById<MaterialButton>(R.id.mbEliminar)
            btnEliminar.setOnClickListener {
                eliminarFotoLocal()
                dialog.dismiss()
            }

            val btnCambiar = dialogView.findViewById<MaterialButton>(R.id.mbCambiar)
            btnCambiar.setOnClickListener {
                pickImageLauncher.launch(arrayOf("image/*"))
                dialog.dismiss()
            }
            true
        }

        btnEmailEdit.setOnClickListener {
            mostrarDialogCambiarEmail()
        }

        btnPasswordEdit.setOnClickListener {
            mostrarDialogCambiarPassword()
        }

        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun guardarImagenInterna(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().filesDir, "profile.jpg")
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            guardarRutaLocal(file.absolutePath)
            mostrarFotoDesdeArchivo(file.absolutePath)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al guardar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarRutaLocal(path: String) {
        val prefs = requireContext().getSharedPreferences("mypet_profile", Context.MODE_PRIVATE)
        prefs.edit().putString("profile_image_path", path).apply()
    }

    private fun cargarFotoGuardada(firebaseUid: String) {
        val prefs = requireContext().getSharedPreferences("mypet_profile", Context.MODE_PRIVATE)
        val path = prefs.getString("profile_image_path", null)

        if (!path.isNullOrEmpty()) {
            mostrarFotoDesdeArchivo(path)
        } else {
            // Si no hay foto local → intentar cargar desde Firestore
            FirestoreHelper.db.collection("usuarios")
                .document(firebaseUid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val usuario = document.toObject(UsuarioFirestore::class.java)

                        val url = usuario?.fotoPerfilUrl ?: ""

                        if (url.isNotEmpty()) {
                            mostrarFotoDesdeUrl(url)
                        } else {
                            ivProfilePic.visibility = View.GONE
                            lottieProfile.visibility = View.VISIBLE
                        }
                    }
                }
                .addOnFailureListener {
                    ivProfilePic.visibility = View.GONE
                    lottieProfile.visibility = View.VISIBLE
                }
        }
    }

    private fun mostrarFotoDesdeUrl(url: String) {
        ivProfilePic.visibility = View.VISIBLE
        lottieProfile.visibility = View.GONE

        Glide.with(this)
            .load(url)
            .circleCrop()
            .into(ivProfilePic)
    }

    private fun mostrarFotoDesdeArchivo(path: String) {
        val file = File(path)

        if (file.exists()) {
            ivProfilePic.setImageURI(Uri.fromFile(file))
            ivProfilePic.visibility = View.VISIBLE
            lottieProfile.visibility = View.GONE
        } else {
            ivProfilePic.visibility = View.GONE
            lottieProfile.visibility = View.VISIBLE
        }
    }

    private fun eliminarFotoLocal() {
        val prefs = requireContext().getSharedPreferences("mypet_profile", Context.MODE_PRIVATE)
        val path = prefs.getString("profile_image_path", null)

        if (!path.isNullOrEmpty()) {
            val file = File(path)
            if (file.exists()) file.delete()
        }

        prefs.edit().remove("profile_image_path").apply()

        ivProfilePic.visibility = View.GONE
        lottieProfile.visibility = View.VISIBLE
    }

    private fun mostrarDialogCambiarPassword() {
        val currentPasswordInput = EditText(requireContext()).apply {
            hint = "Contraseña actual"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val newPasswordInput = EditText(requireContext()).apply {
            hint = "Nueva contraseña"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val confirmPasswordInput = EditText(requireContext()).apply {
            hint = "Confirmar nueva contraseña"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(currentPasswordInput)
            addView(newPasswordInput)
            addView(confirmPasswordInput)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar contraseña")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val currentPassword = currentPasswordInput.text.toString().trim()
                val newPassword = newPasswordInput.text.toString().trim()
                val confirmPassword = confirmPasswordInput.text.toString().trim()

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(requireContext(), "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword.length < 6) {
                    Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                cambiarPassword(currentPassword, newPassword)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cambiarPassword(currentPassword: String, newPassword: String) {
        val user = AuthHelper.auth.currentUser

        if (user == null || user.email.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                Log.d("PASSWORD_CHANGE", "Reautenticación exitosa")

                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        Log.d("PASSWORD_CHANGE", "Contraseña actualizada correctamente")
                        Toast.makeText(requireContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("PASSWORD_CHANGE", "Error al actualizar contraseña", e)
                        Toast.makeText(
                            requireContext(),
                            "Error al actualizar contraseña: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PASSWORD_CHANGE", "Error al reautenticar", e)
                Toast.makeText(
                    requireContext(),
                    "Contraseña actual incorrecta: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun mostrarDialogCambiarEmail() {
        val newEmailInput = EditText(requireContext()).apply {
            hint = "Nuevo correo"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        val currentPasswordInput = EditText(requireContext()).apply {
            hint = "Contraseña actual"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(newEmailInput)
            addView(currentPasswordInput)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar correo")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val newEmail = newEmailInput.text.toString().trim()
                val currentPassword = currentPasswordInput.text.toString().trim()

                if (newEmail.isEmpty() || currentPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                cambiarEmail(currentPassword, newEmail)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cambiarEmail(currentPassword: String, newEmail: String) {
        val user = AuthHelper.auth.currentUser

        if (user == null || user.email.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                Log.d("EMAIL_CHANGE", "Reautenticación exitosa")

                user.verifyBeforeUpdateEmail(newEmail)
                    .addOnSuccessListener {
                        Log.d("EMAIL_CHANGE", "Correo de verificación enviado a: $newEmail")
                        Toast.makeText(
                            requireContext(),
                            "Se envió un correo de verificación al nuevo email",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("EMAIL_CHANGE", "Error verifyBeforeUpdateEmail", e)
                        Toast.makeText(
                            requireContext(),
                            "Error al solicitar cambio de correo: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("EMAIL_CHANGE", "Error al reautenticar", e)
                Toast.makeText(
                    requireContext(),
                    "Contraseña actual incorrecta: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun sincronizarEmailAuthConFirestore(onComplete: () -> Unit) {
        val currentUser = AuthHelper.auth.currentUser

        if (currentUser == null || currentUser.email.isNullOrEmpty()) {
            onComplete()
            return
        }

        val authEmail = currentUser.email!!

        FirestoreHelper.db.collection("usuarios")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val firestoreEmail = document.getString("email")

                if (firestoreEmail != authEmail) {
                    FirestoreHelper.db.collection("usuarios")
                        .document(currentUser.uid)
                        .update("email", authEmail)
                        .addOnSuccessListener {
                            Log.d("EMAIL_SYNC", "Firestore sincronizado con Auth: $authEmail")
                            onComplete()
                        }
                        .addOnFailureListener { e ->
                            Log.e("EMAIL_SYNC", "Error al sincronizar Firestore", e)
                            onComplete()
                        }
                } else {
                    onComplete()
                }
            }
            .addOnFailureListener { e ->
                Log.e("EMAIL_SYNC", "Error al leer Firestore", e)
                onComplete()
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
}
