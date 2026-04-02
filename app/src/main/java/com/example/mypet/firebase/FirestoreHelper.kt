package com.example.mypet.firebase

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
}