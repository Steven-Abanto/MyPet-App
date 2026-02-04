package com.example.mypet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val tvContent = findViewById<android.widget.TextView>(R.id.tvContent)

        //Se marca "Inicio" por defecto
        bottomNavigationView.selectedItemId = R.id.navigation_home

        //Configurar el listener para los clicks
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_memo -> {
                    //Aquí se debe añadir el listener para viajar a la pantalla de Recordatorios
                    //Se añade texto genérico para prueba
                    tvContent.text = "Recordatorios"
                    true
                }
                R.id.navigation_home -> {
                    //Aquí se debe añadir el listener para viajar a la pantalla de Inicio
                    //Se añade texto genérico para prueba
                    tvContent.text = "Inicio"
                    true
                }
                R.id.navigation_profile -> {
                    //Aquí se debe añadir el listener para viajar a la pantalla de Perfil
                    //Se añade texto genérico para prueba
                    tvContent.text = "Perfil"
                    true
                }
                else -> false
            }
        }

    }
}