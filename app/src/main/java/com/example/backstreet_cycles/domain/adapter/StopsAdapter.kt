package com.example.backstreet_cycles.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.ConvertHelper

class StopsAdapter(private val stops: MutableList<Locations>) :
    RecyclerView.Adapter<StopsAdapter.StopViewHolder>() {

    private val collapseBottomSheet: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var clickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    /**
     * @param listener - a listener that allows user to perform an action
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    /**
     * An inner class that creates the journey expandable cards for the stops that the user has been on.
     * Eventually, the cards are put into a recycler view.
     *
     * @param itemView - a view object holding the card view
     * @param listener - a listener that allows user to perform an action on the card
     */
    inner class StopViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.homepage_LocationDataCardName)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    /**
     * Creates a card for all the stops and adds them to a ViewHolder and makes them clickable
     *
     * @param parent - a view object holding the card view
     * @param viewType - an Int
     * @return a JourneyViewHolder - the view holder that holds the recent journey cards
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.location_card, parent, false)
        return StopViewHolder(view, clickListener)
    }

    /**
     * Fills in the StopViewHolder with the information of the stops (name and address)
     *
     * @param holder - an object of StopViewHolder that holds the location names
     * @param position - an integer referring to the position of the element in the list
     */
    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        val newList = stops[position]
        val addressName = ConvertHelper.shortenName(newList.name)
        holder.name.text = addressName.first()
        collapseBottomSheet.value = true
    }

    /**
     * @return the size of stops list.
     */
    override fun getItemCount(): Int {
        return stops.size
    }

    /**
     * @return a list of booleans that allow functionality if they are true or false
     */
    fun getCollapseBottomSheet(): LiveData<Boolean> {
        return collapseBottomSheet
    }

}