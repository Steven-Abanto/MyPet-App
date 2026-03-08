package com.example.mypet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var tilEmail : TextInputLayout
    private lateinit var etEmail : TextInputEditText
    private lateinit var tilPassword : TextInputLayout
    private lateinit var etPassword : TextInputEditText
    private lateinit var btnLogin : Button
    private lateinit var btnRegister : Button
    private lateinit var tvRecoverPassword : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        var tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        var etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        var tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        var etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        var btnLogin = findViewById<Button>(R.id.btnLogin)
        var btnRegister = findViewById<Button>(R.id.btnRegister)
        var tvRecoverPassword = findViewById<TextView>(R.id.tvRecoverPassword)


        btnLogin.setOnClickListener{
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            var isValid = true

            if (email.isEmpty()) {
                tilEmail.error = "Ingresa tu correo"
                isValid = false
            } else {
                tilEmail.error = null
            }

            if (password.isEmpty()) {
                tilPassword.error = "Ingresa tu contraseña"
                isValid = false
            } else {
                tilPassword.error = null
            }

            if (isValid) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvRecoverPassword.setOnClickListener {
            val intent = Intent(this, RecoverActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}