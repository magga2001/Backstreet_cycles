package com.example.backstreet_cycles.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.ConvertHelper

class JourneyHistoryAdapter(
    private var locations: MutableList<List<Locations>>
) : RecyclerView.Adapter<JourneyHistoryAdapter.JourneyViewHolder>() {

    private lateinit var clickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    /**
     * @param listener - an object of OnItemClickListener that allows user to perform an action
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    /**
     * An inner class that creates the recent journey cards for the stops that the user has been on.
     * Eventually, the cards are put into a recycler view.
     *
     * @param itemView - a view object holding the card view
     * @param listener - a listener that allows user to perform an action on the card
     */
    inner class JourneyViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.recentJourneyCardTextView)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    /**
     * Creates a card for all the past journeys and adds them to a ViewHolder and makes them clickable
     *
     * @param parent - A view group that is clickable
     * @param viewType - an Int
     * @return a JourneyViewHolder - the view holder that holds the recent journey cards
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recent_journey_card, parent, false)
        return JourneyViewHolder(view, clickListener)
    }

    /**
     * Fills in the JourneyViewHolder with the information of the locations (name and address)
     *
     * @param holder - an object of JourneyViewHolder that holds the location names
     * @param position - an integer referring to the position of the element in the list
     */
    override fun onBindViewHolder(holder: JourneyViewHolder, position: Int) {
        val newList = locations[position]
        val addressName = ConvertHelper.getLocationNames(newList)
        holder.name.text = constructString(addressName)
    }

    /**
     * Creates a string from all the locations the user has visited in a journey.
     *
     * @param newList - a list of strings that are the location names.
     * @return a string - of all the locations the user has stopped by.
     */
    private fun constructString(newList: List<String>): String {
        var str = ""
        for (i in newList.indices) {
            str += "\n ${i + 1}: ${ConvertHelper.shortenName(newList[i]).first()}"
        }
        return str
    }

    /**
     * @return the size of locations list.
     */
    override fun getItemCount(): Int {
        return locations.size
    }

}