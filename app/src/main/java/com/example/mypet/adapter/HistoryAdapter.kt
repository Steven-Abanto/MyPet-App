package com.example.mypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.R
import com.example.mypet.entity.mappers.HistorialRecordatorioDetalle

class HistoryAdapter(
    private val historyList: List<HistorialRecordatorioDetalle>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloHistorial)
        val tvMascota: TextView = itemView.findViewById(R.id.tvMascotaHistorial)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstadoHistorial)
        val tvFechaProgramada: TextView = itemView.findViewById(R.id.tvFechaProgramadaHistorial)
        val tvFechaCompletado: TextView = itemView.findViewById(R.id.tvFechaCompletadoHistorial)
        val tvNotas: TextView = itemView.findViewById(R.id.tvNotasHistorial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_reminder, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]

        holder.tvTitulo.text = item.tituloRecordatorio
        holder.tvMascota.text = "Mascota: ${item.nombreMascota}"
        holder.tvEstado.text = "Estado: ${item.estado}"
        holder.tvFechaProgramada.text = "Fecha programada: ${item.fechaProgramada}"
        holder.tvFechaCompletado.text = "Fecha completado: ${item.fechaCompletado ?: "Sin fecha"}"
        holder.tvNotas.text = "Notas: ${item.notas ?: "Sin notas"}"
    }
}