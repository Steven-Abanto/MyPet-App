package com.example.mypet

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mypet.entity.firestore.UsuarioFirestore
import com.example.mypet.firebase.AuthHelper
import com.example.mypet.firebase.FirestoreHelper
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etLastNameP = findViewById<TextInputEditText>(R.id.etLastNameP)
        val etLastNameM = findViewById<TextInputEditText>(R.id.etLastNameM)
        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val tilConfirmPassword = findViewById<TextInputLayout>(R.id.tilConfirmPassword)
        val radioGroupPronouns = findViewById<RadioGroup>(R.id.idRadioGroupPronouns)
        val termsCheckBox = findViewById<MaterialCheckBox>(R.id.termsCheckBox)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

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

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val lastNameP = etLastNameP.text.toString().trim()
            val lastNameM = etLastNameM.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val pronoun = when (radioGroupPronouns.checkedRadioButtonId) {
                R.id.idPronombreEl -> "Él"
                R.id.idPronombreElla -> "Ella"
                R.id.idPronombreOtro -> "Otro"
                else -> ""
            }
            val fecha = etFecha.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val isTermsAccepted = termsCheckBox.isChecked
            val isPronounSelected = radioGroupPronouns.checkedRadioButtonId != -1

            if (name.isEmpty() || lastNameP.isEmpty() || lastNameM.isEmpty() ||
                email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                !isPronounSelected || !isTermsAccepted) {
                Toast.makeText(this, "Por favor, completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                tilConfirmPassword.error = "Las contraseñas no coinciden"
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                tilConfirmPassword.error = null
            }

            AuthHelper.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = AuthHelper.auth.currentUser

                        if (firebaseUser == null) {
                            Toast.makeText(this, "No se pudo obtener el usuario creado", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }

                        val usuarioFirestore = UsuarioFirestore(
                            uid = firebaseUser.uid,
                            nombres = name,
                            apellidoPaterno = lastNameP,
                            apellidoMaterno = lastNameM,
                            email = email,
                            telefono = phone,
                            fechaNacimiento = fecha,
                            pronombre = pronoun,
                            activo = true
                        )

                        FirestoreHelper.db.collection("usuarios")
                            .document(firebaseUser.uid)
                            .set(usuarioFirestore)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Usuario creado, pero error al guardar perfil: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.message ?: "Error al registrar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}