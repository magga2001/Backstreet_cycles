package com.example.backstreet_cycles.domain.useCase

import android.content.Context
import android.graphics.Bitmap
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.domain.utils.BitmapHelper
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

object MapAnnotationUseCase {

    private var pointAnnotationManager: PointAnnotationManager ?= null

    //Add list and drawable argument later...
    fun addAnnotationToMap(context: Context, annotationApi: AnnotationPlugin) {

        // Create an instance of the Annotation API and get the PointAnnotationManager.
        val raw_bitmap = BitmapHelper.bitmapFromDrawableRes(context, R.drawable.dock_station) as Bitmap
        val bitmap = Bitmap.createScaledBitmap(raw_bitmap, 150, 150, false)
        bitmap.let {
            // Set options for the resulting symbol layer.
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            for(i in BackstreetApplication.wayPoints.indices)
            {
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    // Define a geographic coordinate.
                    .withPoint(BackstreetApplication.wayPoints[i])
                    // Specify the bitmap you assigned to the point annotation
                    // The bitmap will be added to map style automatically.
                    .withIconImage(it)
                    .withTextAnchor(textAnchor = TextAnchor.TOP)
                    .withTextField((i + 65).toChar().toString())
                    .withTextSize(10.00)
                // Add the resulting pointAnnotation to the map.
                pointAnnotationManager!!.create(pointAnnotationOptions)
            }
        }
    }

    fun removeAnnotations()
    {
        if(pointAnnotationManager != null)
        {
            pointAnnotationManager!!.deleteAll()
        }
    }
}