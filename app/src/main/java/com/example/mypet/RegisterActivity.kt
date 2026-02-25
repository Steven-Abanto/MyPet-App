package com.example.mypet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val tilConfirmPassword = findViewById<TextInputLayout>(R.id.tilConfirmPassword)
        val radioGroupPronouns = findViewById<RadioGroup>(R.id.idRadioGroupPronouns)
        val termsCheckBox = findViewById<MaterialCheckBox>(R.id.termsCheckBox)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val lastNameP = etLastNameP.text.toString().trim()
            val lastNameM = etLastNameM.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val isTermsAccepted = termsCheckBox.isChecked
            val isPronounSelected = radioGroupPronouns.checkedRadioButtonId != -1

            tilConfirmPassword.error = null

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
            }

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}