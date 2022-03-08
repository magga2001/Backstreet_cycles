package com.example.backstreet_cycles.interfaces

import com.example.backstreet_cycles.dto.Locations

interface PlannerInterface {

    fun onSelectedStop(location: Locations)
}