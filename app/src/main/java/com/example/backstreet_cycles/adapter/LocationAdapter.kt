package com.example.backstreet_cycles.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.model.LocationData
import com.example.backstreet_cycles.viewModel.HomePageViewModel

class LocationAdapter(val c: Context, val locationList: ArrayList<LocationData>):RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(){

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener

    }
    inner class LocationViewHolder(itemView: View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.name)
        val latitude = itemView.findViewById<TextView>(R.id.latitude)
        val longitude = itemView.findViewById<TextView>(R.id.longitude)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.location_data,parent, false)
        return LocationViewHolder(v,mListener)
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