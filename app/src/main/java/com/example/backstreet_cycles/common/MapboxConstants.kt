package com.example.backstreet_cycles.common

import android.content.res.Resources
import com.mapbox.maps.EdgeInsets


/**
 * Class holding all the constant data used for MapBox
 */
object MapboxConstants {

    /**
     * Below are generated camera padding values to ensure that the route fits well on screen while
     * other elements are overlaid on top of the map (including instruction view, buttons, etc.)
     */

    private val pixelDensity = Resources.getSystem().displayMetrics.density

    val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    val landscapeOverviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            20.0 * pixelDensity
        )
    }
    val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    val landscapeFollowingPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }

    const val GEO_JSON_SOURCE_LAYER_ID = "GeoJsonSourceLayerId"
    const val SYMBOL_ICON_ID = "SymbolIconId"
    const val SYMBOL_LAYER_ID = "SYMBOL_LAYER_ID"

    //PROFILE CONSTANT

    const val CYCLING = "cycling"
    const val WALKING = "walking"
}