package com.example.mypet.entity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mypet.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RecoverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recover)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInputLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val btnSendRecover = findViewById<Button>(R.id.btnSendRecover)
        val tvBackToLogin = findViewById<TextView>(R.id.tvBackToLogin)

        btnSendRecover.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailInputLayout.error = "Por favor, ingresa tu correo"
            } else {
                emailInputLayout.error = null
                Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show()
            }
        }

        tvBackToLogin.setOnClickListener {
            finish()
        }
    }
}