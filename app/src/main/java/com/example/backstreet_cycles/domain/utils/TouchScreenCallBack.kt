package com.example.backstreet_cycles.domain.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Class for managing cards in recycler view
 */
abstract class TouchScreenCallBack : ItemTouchHelper.Callback() {

    /**
     * Enable the movement of the cards inside the recycler view
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val position = viewHolder.absoluteAdapterPosition
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlag = disableFlags(position)


        return makeMovementFlags(dragFlags, swipeFlag)
    }

    /**
     * Delete card in the recycler view by swiping to the left, disable deletion
     * of the first card
     */
    private fun disableFlags(position: Int): Int {
        return if (position == 0) 0
        else ItemTouchHelper.LEFT
    }

    /**
     * Enable the card swipe to the left in the recycler view
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

}