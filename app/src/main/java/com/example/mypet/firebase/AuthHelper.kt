package com.example.mypet.firebase

import com.google.firebase.auth.FirebaseAuth

object AuthHelper {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
}