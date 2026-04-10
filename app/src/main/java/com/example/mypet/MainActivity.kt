package com.example.mypet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mypet.dao.UsuarioDAO
import com.example.mypet.firebase.AuthHelper
import com.example.mypet.repository.MascotaFirestoreRepository
import com.example.mypet.repository.UsuarioFirestoreRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var tvRecoverPassword: TextView

    private lateinit var ivGoogle : ImageView
    private lateinit var googleClient : GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    private var loginGoogleEnProceso = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        tilEmail = findViewById(R.id.tilEmail)
        etEmail = findViewById(R.id.etEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        tvRecoverPassword = findViewById(R.id.tvRecoverPassword)
        ivGoogle = findViewById(R.id.ivGoogle)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this, gso)

        btnLogin.setOnClickListener {
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

            if (!isValid) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            AuthHelper.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = AuthHelper.auth.currentUser

                        if (firebaseUser == null) {
                            btnLogin.isEnabled = true
                            Toast.makeText(this, "No se pudo obtener el usuario autenticado", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }

                        sincronizarUsuarioYMascotasYNavegar(firebaseUser.uid)
                    } else {
                        btnLogin.isEnabled = true
                        Toast.makeText(
                            this@MainActivity,
                            task.exception?.message ?: "Correo o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        ivGoogle.setOnClickListener {
            loginGoogleEnProceso = true
            startActivityForResult(googleClient.signInIntent, 1001)
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

    override fun onStart() {
        super.onStart()

        if (loginGoogleEnProceso) return

        val currentUser = AuthHelper.auth.currentUser
        if (currentUser != null) {
            sincronizarUsuarioYMascotasYNavegar(currentUser.uid)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        val firebaseUser = auth.currentUser
                        if (firebaseUser == null) {
                            loginGoogleEnProceso = false
                            Toast.makeText(this, "No se pudo obtener el usuario autenticado", Toast.LENGTH_LONG).show()
                            return@addOnSuccessListener
                        }

                        val usuarioRepository = UsuarioFirestoreRepository()

                        usuarioRepository.registrarOSincronizarUsuarioGoogle(this, firebaseUser) { ok, error ->
                            runOnUiThread {
                                if (!ok) {
                                    loginGoogleEnProceso = false
                                    Toast.makeText(
                                        this,
                                        "Error al registrar/sincronizar usuario Google: $error",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@runOnUiThread
                                }
                                loginGoogleEnProceso = false
                                sincronizarUsuarioYMascotasYNavegar(firebaseUser.uid)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        loginGoogleEnProceso = false
                        Toast.makeText(this, "Firebase error: ${e.message}", Toast.LENGTH_LONG).show()
                    }

            } catch (e: ApiException) {
                loginGoogleEnProceso = false
                Toast.makeText(this, "Google Sign-In error: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sincronizarUsuarioYMascotasYNavegar(firebaseUid: String) {
        val usuarioRepository = UsuarioFirestoreRepository()
        val usuarioDAO = UsuarioDAO(this)
        val mascotaRepository = MascotaFirestoreRepository(this)

        usuarioRepository.sincronizarUsuarioALocal(this, firebaseUid) { okUsuario, errorUsuario ->
            runOnUiThread {
                if (!okUsuario) {
                    btnLogin.isEnabled = true
                    Toast.makeText(
                        this,
                        "Sesión iniciada, pero no se pudo sincronizar el usuario: $errorUsuario",
                        Toast.LENGTH_LONG
                    ).show()
                    return@runOnUiThread
                }

                val usuarioLocal = usuarioDAO.obtenerPorFirebaseUid(firebaseUid)
                if (usuarioLocal == null) {
                    btnLogin.isEnabled = true
                    Toast.makeText(
                        this,
                        "No se encontró el usuario sincronizado en la base local",
                        Toast.LENGTH_LONG
                    ).show()
                    return@runOnUiThread
                }

                mascotaRepository.sincronizarMascotasDeUsuarioALocal(
                    firebaseUid = firebaseUid,
                    idUsuarioLocal = usuarioLocal.idUsuario
                ) { okMascotas, errorMascotas ->
                    runOnUiThread {
                        btnLogin.isEnabled = true

                        if (!okMascotas) {
                            Toast.makeText(
                                this,
                                "Sesión iniciada, pero no se pudo sincronizar las mascotas: $errorMascotas",
                                Toast.LENGTH_LONG
                            ).show()
                            return@runOnUiThread
                        }

                        val firebaseUser = AuthHelper.auth.currentUser

                        val sharedPreferences = getSharedPreferences("mypet_session", MODE_PRIVATE)
                        sharedPreferences.edit()
                            .putString("firebaseUid", firebaseUser?.uid)
                            .putString("email", firebaseUser?.email)
                            .apply()

                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}