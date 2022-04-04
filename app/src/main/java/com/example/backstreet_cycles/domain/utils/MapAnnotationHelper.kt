package com.example.backstreet_cycles.domain.utils

import android.content.Context
import android.graphics.Bitmap
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

object MapAnnotationHelper {

    private var pointAnnotationManager: PointAnnotationManager? = null

    /**
     * Function to add annotation to Map
     */
    fun addAnnotationToMap(
        context: Context,
        locations: MutableList<Locations>,
        annotationApi: AnnotationPlugin,
        state: JourneyState
    ) {
        val bitmaps = constructMarker(context, locations, state)

        buildAnnotationToMap(locations, annotationApi, bitmaps)
    }

    /**
     * Function to remove annotation from Map
     */
    fun removeAnnotations() {
        if (pointAnnotationManager != null) {
            pointAnnotationManager!!.deleteAll()
        }
    }

    /**
     * Create annotations
     */
    private fun buildAnnotationToMap(
        locations: MutableList<Locations>,
        annotationApi: AnnotationPlugin,
        bitmaps: List<Bitmap>
    ) {

        // Create an instance of the Annotation API and get the PointAnnotationManager.
        pointAnnotationManager = annotationApi.createPointAnnotationManager()

        for (i in locations.indices) {
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                // Define a geographic coordinate.
                .withPoint(ConvertHelper.convertLocationToPoint(locations[i]))
                // Specify the bitmap you assigned to the point annotation
                // The bitmap will be added to map style automatically.
                .withIconImage(bitmaps[i])
                .withIconAnchor(iconAnchor = IconAnchor.BOTTOM)
                .withTextField(ConvertHelper.shortenName(locations[i].name).first())
                .withTextAnchor(textAnchor = TextAnchor.TOP)
                .withTextSize(13.00)
                .withTextLetterSpacing(0.2)
                .withTextColor("black")

            // Add the resulting pointAnnotation to the map.
            pointAnnotationManager!!.create(pointAnnotationOptions)
        }
    }

    /**
     * Create marker
     */
    private fun constructMarker(
        context: Context,
        locations: MutableList<Locations>,
        state: JourneyState
    ): List<Bitmap> {
        val image = when (state) {
            JourneyState.START_WALKING -> constructImage(
                context,
                listOf(R.drawable.redmapmarker, R.drawable.redcycledock)
            )
            JourneyState.BIKING -> constructImage(
                context,
                listOf(R.drawable.redcycledock, R.drawable.redcycledock)
            )
            JourneyState.END_WALKING -> constructImage(
                context,
                listOf(R.drawable.redcycledock, R.drawable.redmapmarker)
            )
            else -> constructImage(
                context,
                locations.map { (R.drawable.ic_baseline_location_on_red_24dp) })
        }

        return image
    }

    /**
     * Create image of the map
     */
    private fun constructImage(context: Context, drawables: List<Int>): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()

        for (drawable in drawables) {
            bitmaps.add(constructBitmap(context, drawable))
        }

        return bitmaps
    }

    /**
     * Create Bitmap object
     */
    private fun constructBitmap(context: Context, drawable: Int): Bitmap {
        val rawBitmap = BitmapHelper.bitmapFromDrawableRes(context, drawable) as Bitmap
        return Bitmap.createScaledBitmap(rawBitmap, 80, 80, false)
    }
}