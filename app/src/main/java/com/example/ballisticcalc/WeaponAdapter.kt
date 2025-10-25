package com.example.ballisticcalc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeaponAdapter(private var profiles: MutableList<WeaponProfile>) :
    RecyclerView.Adapter<WeaponAdapter.ViewHolder>() {

    fun updateList(newList: MutableList<WeaponProfile>) {
        profiles.clear()
        profiles.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weapon_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(profiles[position])
    }

    override fun getItemCount() = profiles.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(R.id.textViewWeaponName)

        fun bind(profile: WeaponProfile) {
            nameView.text = "${profile.weaponName} + ${profile.projectileName}"
        }
    }
}