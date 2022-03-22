package com.example.backstreet_cycles.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.DTO.Locations

class StopsAdapter(private val stops: MutableList<Locations>):RecyclerView.Adapter<StopsAdapter.StopViewHolder>(){

    private lateinit var clickListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }
    inner class StopViewHolder(itemView: View, listener: OnItemClickListener):RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.card_name)

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
        val addressName = shortenName(newList.name)
        holder.name.text = addressName.first()
    }

    override fun getItemCount(): Int {
        return stops.size
    }

    private fun shortenName(name: String): List<String> {
        val delimiter = ","
        return name.split(delimiter)
    }

}