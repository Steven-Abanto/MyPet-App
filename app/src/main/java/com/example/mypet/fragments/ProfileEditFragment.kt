package com.example.mypet.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.example.mypet.R
import com.example.mypet.dao.UsuarioDAO
import com.example.mypet.entity.Usuario
import com.example.mypet.entity.firestore.UsuarioFirestore
import com.example.mypet.firebase.AuthHelper
import com.example.mypet.firebase.FirestoreHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class ProfileEditFragment : Fragment(R.layout.fragment_profile_edit) {
    private lateinit var usuarioDAO: UsuarioDAO
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

//                guardarUriFoto(uri.toString())
//                mostrarFotoPerfil(uri)
                guardarImagenInterna(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuarioDAO = UsuarioDAO(requireContext())

        val btnSave = view.findViewById<MaterialButton>(R.id.btnProfileEditEditSave)
        val etFecha = view.findViewById<TextInputEditText>(R.id.etFecha)
        val actvGender = view.findViewById<AutoCompleteTextView>(R.id.actvGender)
        val etProfileEditName = view.findViewById<TextInputEditText>(R.id.etProfileEditName)
        val etProfileEditLastname = view.findViewById<TextInputEditText>(R.id.etProfileEditLastname)
        val etProfileEditEmail = view.findViewById<TextInputEditText>(R.id.etProfileEditEmail)
        val etProfileEditPhone = view.findViewById<TextInputEditText>(R.id.etProfileEditPhone)

        ivProfilePic = view.findViewById(R.id.ivProfilePicEdit)
        lottieProfile = view.findViewById(R.id.lottieProfileEdit)
        profilePicContainer = view.findViewById(R.id.profileEditPicContainer)

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
                        etProfileEditName.setText(usuario.nombres)
                        etProfileEditLastname.setText("${usuario.apellidoPaterno} ${usuario.apellidoMaterno}")
                        etFecha.setText(usuario.fechaNacimiento)
                        actvGender.setText(usuario.pronombre, false)
                        etProfileEditPhone.setText(usuario.telefono)

                        // El email ya no se edita aquí
                        etProfileEditEmail.setText(usuario.email)
                        etProfileEditEmail.isEnabled = false
                        etProfileEditEmail.isFocusable = false
                        etProfileEditEmail.isClickable = false
                    } else {
                        Toast.makeText(requireContext(), "No se pudo cargar el usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                val usuarioLocal = usuarioDAO.obtenerPorFirebaseUid(firebaseUid)

                if (usuarioLocal != null) {
                    etProfileEditName.setText(usuarioLocal.nombres)
                    etProfileEditLastname.setText("${usuarioLocal.apellidoPaterno} ${usuarioLocal.apellidoMaterno}")
                    etFecha.setText(usuarioLocal.fechaNacimiento)
                    actvGender.setText(usuarioLocal.pronombre, false)
                    etProfileEditPhone.setText(usuarioLocal.telefono)

                    etProfileEditEmail.setText(usuarioLocal.email)
                    etProfileEditEmail.isEnabled = false
                    etProfileEditEmail.isFocusable = false
                    etProfileEditEmail.isClickable = false

                    Toast.makeText(requireContext(), "Mostrando datos locales (modo offline)", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar los datos: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

        profilePicContainer.setOnClickListener {
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

        btnSave.setOnClickListener {
            val nombres = etProfileEditName.text.toString().trim()
            val apellidosTexto = etProfileEditLastname.text.toString().trim()
            val telefono = etProfileEditPhone.text.toString().trim()
            val fechaNacimiento = etFecha.text.toString().trim()
            val pronombre = actvGender.text.toString().trim()
            val emailActual = currentUser.email ?: etProfileEditEmail.text.toString().trim()

            if (nombres.isEmpty() || apellidosTexto.isEmpty()) {
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
                "telefono" to telefono,
                "fechaNacimiento" to fechaNacimiento,
                "pronombre" to pronombre,
                "activo" to true
            )

            FirestoreHelper.db.collection("usuarios")
                .document(firebaseUid)
                .update(dataActualizada)
                .addOnSuccessListener {
                    val usuarioLocal = Usuario(
                        idUsuario = 0,
                        firebaseUid = firebaseUid,
                        nombres = nombres,
                        apellidoPaterno = apeP,
                        apellidoMaterno = apeM,
                        email = emailActual,
                        telefono = telefono,
                        fechaNacimiento = fechaNacimiento,
                        pronombre = pronombre,
                        activo = true
                    )

                    usuarioDAO.guardarOActualizar(usuarioLocal)

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

    private fun cargarFotoGuardada() {
        val prefs = requireContext().getSharedPreferences("mypet_profile", Context.MODE_PRIVATE)
        val path = prefs.getString("profile_image_path", null)

        if (!path.isNullOrEmpty()) {
            mostrarFotoDesdeArchivo(path)
        } else {
            ivProfilePic.visibility = View.GONE
            lottieProfile.visibility = View.VISIBLE
        }
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
}