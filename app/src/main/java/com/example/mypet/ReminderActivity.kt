package com.example.mypet

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class ReminderActivity : AppCompatActivity() {
    private lateinit var lvReminders : ListView
    private var listaReminders = mutableListOf<String>()
    private lateinit var listAdapter : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reminder)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupBottomNavigation()
        lvReminders = findViewById(R.id.lvReminders)

        listAdapter = ArrayAdapter(
            this, //Activity donde se va a ejeutar
            android.R.layout.simple_list_item_1, //Diseño reutilizado para cada elemento
            listaReminders //Lista con datos
        )

        lvReminders.adapter = listAdapter

        val ivAddReminder = findViewById<ImageView>(R.id.ivAddReminder)

        ivAddReminder.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.modal_add_reminder, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()
            dialog.show()
            true

            val etReminder = dialogView.findViewById<EditText>(R.id.etReminder)
            etReminder.text.clear()

            val btnCancelarReminder = dialogView.findViewById<Button>(R.id.btnCancelarReminder)
            btnCancelarReminder.setOnClickListener {
                dialog.dismiss()
            }

            val btnGuardarReminder = dialogView.findViewById<Button>(R.id.btnGuardarReminder)
            btnGuardarReminder.setOnClickListener {
                etReminder.text.trim()
                if (etReminder.text.isNotEmpty()) {
                    val reminder = etReminder.text.toString()
                    listaReminders.add(reminder)
                    listAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }

            lvReminders.setOnItemLongClickListener { _, _, position, _ ->
                val itemReminder = listaReminders[position]

                val dialogView = layoutInflater.inflate(R.layout.modal_options, null)
                val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTitulo)
                tvTitulo.text = "Opciones para $itemReminder"

                val dialog = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create()
                dialog.show()

                val btnEliminar = dialogView.findViewById<MaterialButton>(R.id.mbEliminar)
                btnEliminar.setOnClickListener {
                    listaReminders.removeAt(position)
                    listAdapter.notifyDataSetChanged()
                    Toast.makeText(this,"Recordatorio eliminado: $itemReminder", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

                true
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_memo -> {
                    true
                }
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, UserActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}