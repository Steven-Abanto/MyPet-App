package com.example.mypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.R
import com.example.mypet.entity.mappers.RecordatorioDetalle

class ReminderAdapter(
    private val reminderList: List<RecordatorioDetalle>,
    private val onLongClick: (RecordatorioDetalle) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivReminderIcon: ImageView = itemView.findViewById(R.id.ivReminderIcon)
        val tvReminderTitle: TextView = itemView.findViewById(R.id.tvReminderTitle)
        val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        val tvReminderType: TextView = itemView.findViewById(R.id.tvReminderType)
        val tvStartDate: TextView = itemView.findViewById(R.id.tvStartDate)
        val tvFrequency: TextView = itemView.findViewById(R.id.tvFrequency)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun getItemCount(): Int = reminderList.size

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminderList[position]

        holder.tvReminderTitle.text = reminder.titulo
        holder.tvPetName.text = reminder.nombreMascota
        holder.tvReminderType.text = reminder.nombreTipoRecordatorio
        holder.tvStartDate.text = reminder.fechaInicio
        holder.tvFrequency.text = reminder.frecuencia ?: "UNA_VEZ"
        holder.tvDescription.text = reminder.descripcion ?: "Sin descripción"

        when (reminder.idTipoRecordatorio) {
            1 -> holder.ivReminderIcon.setImageResource(R.drawable.ic_vaccine)
            2 -> holder.ivReminderIcon.setImageResource(R.drawable.ic_medicine)
            3 -> holder.ivReminderIcon.setImageResource(R.drawable.ic_bath)
            4 -> holder.ivReminderIcon.setImageResource(R.drawable.ic_food)
            else -> holder.ivReminderIcon.setImageResource(R.drawable.ic_reminder)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(reminder)
            true
        }
    }
}