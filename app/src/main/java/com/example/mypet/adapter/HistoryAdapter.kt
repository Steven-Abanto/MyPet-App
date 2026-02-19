package com.example.mypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.R
import com.example.mypet.entity.Compra

class HistoryAdapter (private val historyList: List<Compra>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val petImage: ImageView = itemView.findViewById(R.id.petH)
        val petName: TextView = itemView.findViewById(R.id.tvName)
        val petAge: TextView = itemView.findViewById(R.id.tvAge)
        val petBreed: TextView = itemView.findViewById(R.id.tvBreed)
        val petWeight: TextView = itemView.findViewById(R.id.tvWeight)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial,parent,false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.petName.text = "ID: #${history.id}"
        holder.petAge.text = history.fecha
        holder.petBreed.text = history.raza
        holder.petWeight.text = history.peso
    }
}