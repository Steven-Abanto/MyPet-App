package com.example.mypet.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.example.mypet.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class ProfileEditFragment : Fragment(R.layout.fragment_profile_edit) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnSave = view.findViewById<MaterialButton>(R.id.btnProfileEditSave)
        val etFecha = view.findViewById<TextInputEditText>(R.id.etFecha)
        val actvGender = view.findViewById<AutoCompleteTextView>(R.id.actvGender)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.gender_options)
        )

        actvGender.setAdapter(adapter)
        actvGender.setOnClickListener {
            actvGender.showDropDown()
        }

        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
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

        btnSave.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}