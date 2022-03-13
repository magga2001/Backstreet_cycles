package com.example.backstreet_cycles.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.dto.Locations

class StopsAdapter(val stops: MutableList<Locations>):RecyclerView.Adapter<StopsAdapter.StopViewHolder>(){

    private lateinit var clickListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){

        clickListener = listener

    }
    inner class StopViewHolder(itemView: View, listener: OnItemClickListener):RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.name)
        val latitude = itemView.findViewById<TextView>(R.id.latitude)
        val longitude = itemView.findViewById<TextView>(R.id.longitude)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.location_data,parent, false)
        return StopViewHolder(view,clickListener)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        val newList = stops[position]
        holder.name.text = newList.name
        holder.latitude.text = newList.lat.toString()
        holder.longitude.text = newList.lon.toString()

    }

    override fun getItemCount(): Int {
        return stops.size
    }



}