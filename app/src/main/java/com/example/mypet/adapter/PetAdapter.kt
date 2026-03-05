package com.example.mypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.R
import com.example.mypet.entity.Pet

class PetAdapter (private val petList: List<Pet>) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val petImage: ImageView = itemView.findViewById(R.id.petImage)
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
        return petList.size
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = petList[position]
        holder.petName.text = pet.name
        holder.petAge.text = pet.birthDate
        holder.petSpecies.text = pet.species
        holder.petBreed.text = pet.breed
        holder.petWeight.text = pet.weight

        when (pet.species) {
            "Perro" -> holder.petImage.setImageResource(R.drawable.ic_dog)
            "Gato" -> holder.petImage.setImageResource(R.drawable.ic_cat)
            else -> holder.petImage.setImageResource(R.drawable.ic_logo_mypet)

        }
    }
}