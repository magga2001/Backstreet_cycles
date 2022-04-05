package com.example.backstreet_cycles.interfaces

import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.JourneyState

/**
 * Interface class for planning journey
 */
interface Planner {

    fun onSelectedJourney(
        location: Locations,
        profile: String,
        locations: MutableList<Locations>,
        state: JourneyState
    )

    fun onFetchJourney(locations: MutableList<Locations>)
}