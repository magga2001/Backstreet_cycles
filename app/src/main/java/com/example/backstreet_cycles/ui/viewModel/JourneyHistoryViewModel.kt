package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.domain.utils.JsonHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * View model for Journey History Activity responsible for storing previous journeys and the current journey if one exists
 */
@HiltViewModel
class JourneyHistoryViewModel @Inject constructor(
    tflRepository: TflRepository,
    mapboxRepository: MapboxRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    application: Application,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(
    tflRepository,
    mapboxRepository,
    cyclistRepository,
    userRepository,
    application,
    applicationContext
) {

    private var stops: MutableList<Locations> = mutableListOf()
    private val isReady: MutableLiveData<Boolean> = MutableLiveData()
    private val message: MutableLiveData<String> = MutableLiveData()
    private var showAlert: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mapboxNavigation by lazy {

        if (MapboxNavigationProvider.isCreated()) {
            MapboxNavigationProvider.retrieve()
        } else {
            MapboxNavigationProvider.create(
                NavigationOptions.Builder(mApplication)
                    .accessToken(mApplication.getString(R.string.mapbox_access_token))
                    .build()
            )
        }
    }

    /**
     * Getter function to obtain the route of journey
     */
    override fun getRoute() {
        super.getRoute()
        setJourneyLocations(getStops())
        checkCurrentJourney()
    }

    /**
     * Checks for a current journey, if not found alert message given
     */
    private fun checkCurrentJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val noCurrentJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!noCurrentJourney) {
            setShowAlert(true)
        } else {
            isReady.value = true
        }
    }

    /**
     * Updates the current location of the user
     * @param currentLocation of user
     */
    fun updateCurrentLocation(currentLocation: Location) {
        for (stop in getStops()) {
            if (stop.name == mContext.getString(R.string.location)) {

                val longitude = currentLocation.longitude
                val latitude = currentLocation.latitude

                stop.lat = latitude
                stop.lon = longitude
            }
        }
    }

    /**
     * Attempts to continue the current journey
     */
    override fun continueWithCurrentJourney() {
        super.continueWithCurrentJourney()
        isReady.value = true
    }

    /**
     * Attempts to set up a new journey
     * @param newStops containing the new searched locations
     */
    override fun continueWithNewJourney(newStops: MutableList<Locations>) {
        super.continueWithNewJourney(newStops)
        isReady.value = true
    }

    /**
     * Getter function to return the past journeys
     * @param userDetails
     * @return MutableList containing the list of journeys with their locations
     */
    fun getJourneyHistory(userDetails: Users): MutableList<List<Locations>> {
        val listLocations = emptyList<List<Locations>>().toMutableList()
        for (journey in userDetails.journeyHistory) {
            val serializedObject: String = journey
            JsonHelper.stringToObject(serializedObject, Locations::class.java)
                ?.let { listLocations.add(it) }
        }
        return listLocations
    }

    /**
     * Adding locations for the journey
     * @param checkpoints
     */
    fun addAllStops(checkpoints: MutableList<Locations>) {
        stops.addAll(checkpoints)
    }

    /**
     * Clearing all locations searched and added by the user
     */
    fun clearAllStops() {
        stops.clear()
    }

    /**
     * Getter function to obtain the list of stops of the journey
     * @return MutableList<Locations> containing the locations of the journey
     */
    fun getStops(): MutableList<Locations> {
        return stops
    }

    /**
     * Getter function to obtain the MapBox Navigation
     * @return MapboxNavigation
     */
    fun getMapBoxNavigation(): MapboxNavigation {
        return mapboxNavigation
    }

    /**
     * Terminate the Mapbox navigation
     */
    fun destroyMapboxNavigation() {
        MapboxNavigationProvider.destroy()
    }

    /**
     * Setting up the show alert
     * @param bool to indicate state
     */
    fun setShowAlert(bool: Boolean) {
        showAlert.value = bool
    }

    /**
     * Getter function to obtain the show alert
     * @return MutableLiveData
     */
    fun getShowAlertMutableLiveData(): MutableLiveData<Boolean> {
        return showAlert
    }

    /**
     * Getter function to obtain the isReady
     * @return isReady
     */
    fun getIsReady(): MutableLiveData<Boolean> {
        return isReady
    }

    /**
     * Getter function to obtain the message
     * @return message
     */
    fun getMessage(): MutableLiveData<String> {
        return message
    }
}