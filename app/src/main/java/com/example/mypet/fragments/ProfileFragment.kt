package com.example.mypet.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mypet.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnEdit = view.findViewById<MaterialButton>(R.id.btnProfileEdit)

        btnEdit.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentProfileContainer, ProfileEditFragment())
                .addToBackStack(null)
                .commit()

        }
    }
}