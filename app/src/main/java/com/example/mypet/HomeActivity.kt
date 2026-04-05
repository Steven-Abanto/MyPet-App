package com.example.mypet

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.adapter.PetAdapter
import com.example.mypet.dao.MascotaDAO
import com.example.mypet.dao.UsuarioDAO
import com.example.mypet.firebase.AuthHelper
import com.example.mypet.repository.MascotaFirestoreRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var rvPets: RecyclerView
    private lateinit var petAdapter: PetAdapter
    private lateinit var mascotaDAO: MascotaDAO
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var mascotaRepository: MascotaFirestoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        mascotaDAO = MascotaDAO(this)
        usuarioDAO = UsuarioDAO(this)
        mascotaRepository = MascotaFirestoreRepository(this)

        rvPets = findViewById(R.id.rvPets)
        rvPets.layoutManager = LinearLayoutManager(this)
        petAdapter = PetAdapter(emptyList())
        rvPets.adapter = petAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNavigation()

        val ivAddPet = findViewById<ImageView>(R.id.ivAddPet)
        ivAddPet.setOnClickListener {
            startActivity(Intent(this, RegisterPetActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarMascotasLocales()
        sincronizarMascotasYRefrescar()
    }

    private fun cargarMascotasLocales() {
        val usuarioLocal = obtenerUsuarioActualLocal() ?: return
        val mascotasConDetalle = mascotaDAO.obtenerMascotasConDetalle(usuarioLocal.idUsuario)
        petAdapter = PetAdapter(mascotasConDetalle)
        rvPets.adapter = petAdapter
    }

    private fun sincronizarMascotasYRefrescar() {
        val firebaseUid = AuthHelper.auth.currentUser?.uid

        if (firebaseUid.isNullOrEmpty()) {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val usuarioLocal = usuarioDAO.obtenerPorFirebaseUid(firebaseUid)
        if (usuarioLocal == null) {
            Toast.makeText(this, "No se encontró el usuario en la base local", Toast.LENGTH_SHORT).show()
            return
        }

        mascotaRepository.sincronizarMascotasDeUsuarioALocal(
            firebaseUid = firebaseUid,
            idUsuarioLocal = usuarioLocal.idUsuario
        ) { ok, error ->
            runOnUiThread {
                if (ok) {
                    val mascotasActualizadas = mascotaDAO.obtenerMascotasConDetalle(usuarioLocal.idUsuario)
                    petAdapter = PetAdapter(mascotasActualizadas)
                    rvPets.adapter = petAdapter
                } else {
                    Toast.makeText(
                        this,
                        "No se pudo actualizar las mascotas: $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun obtenerUsuarioActualLocal() =
        AuthHelper.auth.currentUser?.uid?.let { usuarioDAO.obtenerPorFirebaseUid(it) }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_memo -> {
                    startActivity(Intent(this, ReminderActivity::class.java))
                    true
                }
                R.id.navigation_home -> true
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}