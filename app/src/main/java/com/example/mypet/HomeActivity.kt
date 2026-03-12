package com.example.mypet

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.adapter.PetAdapter
import com.example.mypet.entity.Mascota
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var rvPets : RecyclerView
    private lateinit var petAdapter: PetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        rvPets = findViewById(R.id.rvPets)
        val mascotas = listOf(
            Mascota(1,1,"Pipa","01-01-2020",1,1,"F","8.5Kg",true,false,"Le gusta dormir mucho",true),
            Mascota(2,1,"Pipe","01-01-2020",2,1,"F","8.5Kg",true,false,"Le gusta dormir mucho",true),
        )

        petAdapter = PetAdapter(mascotas)

        rvPets.layoutManager = LinearLayoutManager(this)
        rvPets.adapter = petAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupBottomNavigation()
        val ivAddPet = findViewById<ImageView>(R.id.ivAddPet)

        ivAddPet.setOnClickListener {
            val intent = Intent(this, RegisterPetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_memo -> {
                    startActivity(Intent(this, ReminderActivity::class.java))
                    true
                }
                R.id.navigation_home -> {
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}