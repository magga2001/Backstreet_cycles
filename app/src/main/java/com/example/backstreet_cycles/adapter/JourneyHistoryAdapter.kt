package com.example.backstreet_cycles.adapter

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.DTO.Locations
import com.example.backstreet_cycles.interfaces.PlannerInterface
import com.example.backstreet_cycles.utils.PlannerHelper

class JourneyHistoryAdapter(private val context: Context, private var locations: List<Locations>): RecyclerView.Adapter<JourneyHistoryAdapter.ViewHolder>() {

    private var viewHolders: MutableList<ViewHolder> = emptyList<ViewHolder>().toMutableList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        internal var journeyButton: Button = view.findViewById(R.id.button_expand)
        internal var cardView: ViewGroup = view.findViewById(R.id.cardView)

        init {
            initListener()
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }

        private fun initListener()
        {
            journeyButton.setOnClickListener {
                // Redirect to plan journey activity
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_recent_journeys_card, parent, false)
        val viewHolder = ViewHolder(view)
        addViewHolder(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]
        holder.journeyButton.text = "locations goes here."
    }

    override fun getItemCount(): Int {
        return locations.size - 1
    }

    fun addViewHolder(viewHolder: ViewHolder) {
        viewHolders.add(viewHolder)
    }
}