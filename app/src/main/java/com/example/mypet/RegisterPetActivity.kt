package com.example.mypet

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mypet.entity.Mascota
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class RegisterPetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_pet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val petNeuteredDateEditText = findViewById<TextInputEditText>(R.id.petNeuteredDateEditText)

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

        petNeuteredDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    petNeuteredDateEditText.setText(date)
                },
                year,
                month,
                day
            )
            datePicker.show()
        }

        btnCancelar.setOnClickListener {
            finish()
        }

    }
}