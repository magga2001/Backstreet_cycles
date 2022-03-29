package com.example.backstreet_cycles.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.PlannerHelper

class JourneyHistoryAdapter(
    private var locations: MutableList<List<Locations>>
) : RecyclerView.Adapter<JourneyHistoryAdapter.JourneyViewHolder>() {

    private lateinit var clickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    inner class JourneyViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.recentJourneyCardTextView)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recent_journey_card, parent, false)
//        locations.reverse()
        return JourneyViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: JourneyViewHolder, position: Int) {
        val newList = locations[position]
        val addressName = getLocationNames(newList)
        holder.name.text = constructString(addressName)
    }

    private fun constructString(newList: List<String>): String {
        var str = ""
        for (i in newList.indices) {
            str += "\n ${i + 1}: ${PlannerHelper.shortenName(newList[i]).first()}"
        }
        return str
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    private fun getLocationNames(locations: List<Locations>): List<String> {
        return locations.map { it.name }
    }

}