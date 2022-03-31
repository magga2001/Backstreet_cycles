package com.example.backstreet_cycles

import android.app.Application
import com.example.backstreet_cycles.data.local.TouristAttractionFile
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository

class FakeLocationRepoImpl : LocationRepository{

    private val locations: MutableList<Locations> = mutableListOf()
    private val location1: Locations = Locations("Tower of London",51.5081,-0.0759)
    private val location2: Locations = Locations("Tate Modern",51.5076,-0.0994 )
    private val location3: Locations = Locations("St Paul's Cathedral",51.5138,-0.0984 )
    private val location4: Locations = Locations("Tower Bridge",51.5055,-0.0754 )
    private val location5: Locations = Locations("Trafalgar Square",51.5080,-0.1281 )
    private val location6: Locations = Locations("Buckingham Palace",51.5014,-0.1419 )
    private val location7: Locations = Locations("The Barbican Centre",51.5202,-0.0938 )
    private val location8: Locations = Locations("Royal Albert Hall",51.5009,-0.1774 )
    private val location9: Locations = Locations("Harrods",51.4994,-0.1632 )
    private val location10: Locations = Locations("Selfridges",51.5144,-0.1528 )

    override fun loadLocations(application: Application) {
        locations.add(location1)
        locations.add(location2)
        locations.add(location3)
        locations.add(location4)
        locations.add(location5)
        locations.add(location6)
        locations.add(location7)
        locations.add(location8)
        locations.add(location9)
        locations.add(location10)
    }

    override fun getTouristLocations(): MutableList<Locations> {
        return locations
    }

}