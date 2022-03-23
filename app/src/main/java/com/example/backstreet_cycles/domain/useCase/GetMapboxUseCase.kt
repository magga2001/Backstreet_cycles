package com.example.backstreet_cycles.domain.useCase

import android.content.Context
import android.util.Log
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.remote.MapboxApi
import com.example.backstreet_cycles.data.remote.TflHelper
import com.example.backstreet_cycles.data.remote.dto.toDock
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.google.android.gms.tasks.Tasks.await
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.navigation.core.MapboxNavigation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetMapboxUseCase @Inject constructor(
    private val mapboxRepository: MapboxRepository
){

    operator fun invoke(mapboxNavigation: MapboxNavigation, routeOptions : RouteOptions, info: Boolean): Flow<DirectionsRoute> = callbackFlow {

        val callBack = object : CallbackResource<DirectionsRoute> {
            override fun getResult(objects: DirectionsRoute) {
                Log.i("current Route MAGGA:", objects.toString())
                trySend(objects)
            }
        }

        mapboxRepository.requestRoute(mapboxNavigation, routeOptions, info, callBack)

        awaitClose { channel.close() }
    }
}