package com.example.mypet.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.R
import com.example.mypet.adapter.HistoryAdapter
import com.example.mypet.dao.HistorialRecordatorioDAO
import com.example.mypet.dao.UsuarioDAO
import com.example.mypet.firebase.AuthHelper
import com.example.mypet.repository.HistorialRecordatorioFirestoreRepository

class ReminderHistoryFragment : Fragment() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var tvTitleHistory: TextView
    private lateinit var historialDAO: HistorialRecordatorioDAO
    private lateinit var historialRepo: HistorialRecordatorioFirestoreRepository
    private lateinit var usuarioDAO: UsuarioDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = requireContext()
        historialDAO = HistorialRecordatorioDAO(ctx)
        historialRepo = HistorialRecordatorioFirestoreRepository(ctx)
        usuarioDAO = UsuarioDAO(ctx)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_reminder_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvHistory = view.findViewById(R.id.rvHistory)
        tvTitleHistory = view.findViewById(R.id.tvTitleHistory)
        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        tvTitleHistory.text = "Historial de recordatorios"

        view.findViewById<ImageView>(R.id.ivBackHistory).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLocal()
        sincronizarHistorial()
    }

    private fun cargarLocal() {
        val usuario = obtenerUsuarioActualLocal()
        if (usuario == null) {
            Toast.makeText(requireContext(), "No se encontró el usuario local", Toast.LENGTH_SHORT).show()
            return
        }

        val lista = historialDAO.obtenerHistorialCompletadoPorUsuario(usuario.idUsuario)
        rvHistory.adapter = HistoryAdapter(lista)
    }

    private fun sincronizarHistorial() {
        val uid = AuthHelper.auth.currentUser?.uid ?: return

        historialRepo.sincronizarHistorialDeUsuarioALocal(uid) { ok, error ->
            activity?.runOnUiThread {
                if (ok) {
                    cargarLocal()
                } else {
                    Toast.makeText(
                        requireContext(),
                        error ?: "No se pudo sincronizar historial",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun obtenerUsuarioActualLocal() =
        AuthHelper.auth.currentUser?.uid?.let { usuarioDAO.obtenerPorFirebaseUid(it) }
}