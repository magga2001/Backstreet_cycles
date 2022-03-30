package com.example.backstreet_cycles.domain.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.JourneyState
import com.example.backstreet_cycles.domain.utils.ConvertHelper
import com.example.backstreet_cycles.interfaces.Planner
import com.example.backstreet_cycles.ui.views.JourneyActivity


class PlanJourneyAdapter(
    private val context: Context,
    private val docks: MutableList<Dock>,
    private var locations: List<Locations>,
    private val planner: Planner
) : RecyclerView.Adapter<PlanJourneyAdapter.ViewHolder>() {

    private var viewHolders: MutableList<ViewHolder> = emptyList<ViewHolder>().toMutableList()
    private val allBoxesCheckedMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val collapseBottomSheet: MutableLiveData<Boolean> = MutableLiveData()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        internal var expandButton: Button = view.findViewById(R.id.planJourney_button_expand)
        internal var checkBoxButton: CheckBox = view.findViewById(R.id.checkBoxFinishJourney)
        internal var expandableLayout: View = view.findViewById(R.id.planJourney_expandableLayout)
        internal var cardView: ViewGroup = view.findViewById(R.id.locationDataCardView)
        internal var setNav1: Button = view.findViewById(R.id.setNav1)
        internal var setNav2: Button = view.findViewById(R.id.setNav2)
        internal var setNav3: Button = view.findViewById(R.id.setNav3)
        internal var tvFrom: TextView = view.findViewById(R.id.planJourney_from)
        internal var tvTo: TextView = view.findViewById(R.id.planJourney_to)

        init {
            initListener()
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }

        private fun initListener() {
            expandButton.setOnClickListener {
                if (expandableLayout.visibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableLayout.visibility = View.VISIBLE
                    expandButton.text = "v"

                } else {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableLayout.visibility = View.GONE
                    expandButton.text = ">"
                }
            }
        }
    }

    /**
     * Creates a card for all the chosen locations and adds them to a ViewHolder and makes them clickable
     *
     * @param parent - a view object holding the card view
     * @param viewType - an Int
     * @return a JourneyViewHolder - the view holder that holds the recent journey cards
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.journey_expandable_card, parent, false)
        val viewHolder = ViewHolder(view)
        addViewHolder(viewHolder)
        return viewHolder
    }

    /**
     * Fills in the ViewHolder with the information of the stops (name and location).
     * Also the implementation of the actions on the set navigation buttons in the journey expandable card.
     *
     * @param holder - an object of StopViewHolder that holds the location names
     * @param position - an integer referring to the position of the element in the list
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val location = locations[position]
        val buttonList: MutableList<Button> = mutableListOf()

        buttonList.add(holder.setNav1)
        buttonList.add(holder.setNav2)
        buttonList.add(holder.setNav3)

        holder.checkBoxButton.setOnClickListener {
            enableExpandButton(holder)
        }

        holder.tvFrom.text = "From: ${ConvertHelper.shortenName(location.name).first()} "

        holder.tvTo.text = "To: ${ConvertHelper.shortenName(locations[position + 1].name).first()}"

        holder.setNav1.setOnClickListener {

            val journeyPoints = PlannerHelper.calcRoutePlanner(
                docks,
                locations[position],
                locations[position + 1],
                1
            )

            planner.onSelectedJourney(
                location, MapboxConstants.WALKING, mutableListOf(
                    journeyPoints["startingPoint"]!!,
                    journeyPoints["pickUpPoint"]!!
                ), JourneyState.START_WALKING
            )
            collapseBottomSheet.postValue(true)
            disableOrEnableNavigationButtons(holder, holder.setNav1)
        }

        holder.setNav2.setOnClickListener {

            val journeyPoints = PlannerHelper.calcRoutePlanner(
                docks,
                locations[position],
                locations[position + 1],
                1
            )

            planner.onSelectedJourney(
                location, MapboxConstants.CYCLING, mutableListOf(
                    journeyPoints["pickUpPoint"]!!,
                    journeyPoints["dropOffPoint"]!!
                ), JourneyState.BIKING
            )
            collapseBottomSheet.postValue(true)
            disableOrEnableNavigationButtons(holder, holder.setNav2)
        }

        holder.setNav3.setOnClickListener {

            val journeyPoints = PlannerHelper.calcRoutePlanner(
                docks,
                locations[position],
                locations[position + 1],
                1
            )

            planner.onSelectedJourney(
                location, MapboxConstants.WALKING, mutableListOf(
                    journeyPoints["dropOffPoint"]!!,
                    journeyPoints["destination"]!!
                ), JourneyState.END_WALKING
            )
            collapseBottomSheet.postValue(true)
            disableOrEnableNavigationButtons(holder, holder.setNav3)
        }
    }

    /**
     * Disables or enables the bset navigation button and the start navigation button depending on the
     * selected journey and the navigation
     *
     * @param holder - a ViewHolder that is clickable
     * @param buttonToDisable - a button that will be enabled or disabled
     */
    private fun disableOrEnableNavigationButtons(holder: ViewHolder, buttonToDisable: Button) {
        holder.setNav1.isEnabled = true
        holder.setNav2.isEnabled = true
        holder.setNav3.isEnabled = true
        if (buttonToDisable.id == holder.setNav1.id) {
            holder.setNav1.isEnabled = false
        } else if (buttonToDisable.id == holder.setNav2.id) {
            holder.setNav2.isEnabled = false
        } else {
            holder.setNav3.isEnabled = false
        }
        (context as JourneyActivity).findViewById<Button>(R.id.start_navigation).isEnabled = true

    }

    /**
     * @return the size of the location list subtracted by one
     */
    override fun getItemCount(): Int {
        return locations.size - 1
    }

    /**
     * Adds another view holder to the view holder list if a location is added
     *
     * @param viewHolder - a ViewHolder object
     */
    private fun addViewHolder(viewHolder: ViewHolder) {
        viewHolders.add(viewHolder)
    }

    /**
     * Checking if all the boxes are checked or not
     */
    fun getAllBoxesCheckedMutableLiveData(): LiveData<Boolean> {
        return allBoxesCheckedMutableLiveData
    }

    /**
     * @return a list of booleans that allow functionality if they are true or false
     */
    fun getCollapseBottomSheet(): LiveData<Boolean> {
        return collapseBottomSheet
    }

    /**
     * Allows the expand button to be disabled or enabled based on the user's action
     *
     * @param holder - An object of ViewHolder
     */
    private fun enableExpandButton(holder: ViewHolder) {
        holder.expandButton.isEnabled = !holder.checkBoxButton.isChecked
        checkCurrentCheckBox(holder)
        checkAllBoxes()
    }

    /**
     * Checking if the box is checked or not
     *
     * @param holder - An object of ViewHolder
     */
    private fun checkCurrentCheckBox(holder: ViewHolder) {
        if (holder.checkBoxButton.isEnabled) {
            holder.expandableLayout.visibility = View.GONE
            holder.expandButton.text = ">"
        }
    }

    /**
     *  Checking if all the boxes are checked or not
     *
     * @param holder - An object of ViewHolder
     */
    private fun checkAllBoxes() {
        if (viewHolders.all { it.checkBoxButton.isChecked }) {
            allBoxesCheckedMutableLiveData.postValue(true)

        } else {
            allBoxesCheckedMutableLiveData.postValue(false)
        }
    }
}