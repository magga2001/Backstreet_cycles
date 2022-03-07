package com.example.backstreet_cycles.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.model.LocationData

class LocationAdapter(val c: Context, val locationList: ArrayList<LocationData>):RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(){
    inner class LocationViewHolder(val v: View):RecyclerView.ViewHolder(v){
        val name = v.findViewById<TextView>(R.id.name)
        val latitude = v.findViewById<TextView>(R.id.latitude)
        val longitude = v.findViewById<TextView>(R.id.longitude)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.location_data,parent, false)
        return LocationViewHolder(v)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val newList = locationList[position]
        holder.name.text = newList.name
        holder.latitude.text = newList.latitude.toString()
        holder.longitude.text = newList.longitude.toString()

    }

    override fun getItemCount(): Int {
        return locationList.size
    }

}