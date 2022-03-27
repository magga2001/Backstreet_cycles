package com.example.backstreet_cycles.domain.useCase

import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.navigation.core.MapboxNavigation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetMapboxUseCase @Inject constructor(
    private val mapboxRepository: MapboxRepository
){

    operator fun invoke(mapboxNavigation: MapboxNavigation, routeOptions : RouteOptions, info: Boolean = false): Flow<DirectionsRoute> = callbackFlow {

        val callBack = object : CallbackResource<DirectionsRoute> {
            override fun getResult(objects: DirectionsRoute) {
                trySend(objects)
            }
        }

        mapboxRepository.requestRoute(mapboxNavigation, routeOptions, info, callBack)

        awaitClose { channel.close() }
    }
}