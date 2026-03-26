package com.example.mypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.mypet.R
import com.example.mypet.entity.MascotaDetalle

class PetAdapter (private val mascotaList: List<MascotaDetalle>) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val petImage: LottieAnimationView = itemView.findViewById(R.id.lavPetImage)
        val petName: TextView = itemView.findViewById(R.id.tvName)
        val petAge: TextView = itemView.findViewById(R.id.tvAge)
        val petSpecies: TextView = itemView.findViewById(R.id.tvSpecies)
        val petBreed: TextView = itemView.findViewById(R.id.tvBreed)
        val petWeight: TextView = itemView.findViewById(R.id.tvWeight)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet,parent,false)
        return PetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mascotaList.size
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = mascotaList[position]
        holder.petName.text = pet.nombres
        holder.petAge.text = pet.fechaNacimiento
        holder.petSpecies.text = pet.nombreEspecie
        holder.petBreed.text = pet.nombreRaza
        holder.petWeight.text = "${pet.pesoActual} Kg"

        when (pet.idEspecie) {
            1 -> holder.petImage.setAnimation(R.raw.dogwalking)
            2 -> holder.petImage.setAnimation(R.raw.loadercat)
        }
    }
}