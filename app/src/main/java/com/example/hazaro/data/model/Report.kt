package com.example.hazaro.data.model

import com.google.android.gms.maps.model.BitmapDescriptorFactory

enum class DisasterType(
    val id: String,
    val label: String,
    val markerHue: Float,
) {
    LANDSLIDE("landslide", "Landslide", BitmapDescriptorFactory.HUE_ORANGE),
    ROAD_CLOSURE("road_closure", "Road closure", BitmapDescriptorFactory.HUE_RED),
    FLOOD("flood", "Flood", BitmapDescriptorFactory.HUE_AZURE),
    OTHER("other", "Other", BitmapDescriptorFactory.HUE_VIOLET);

    companion object {
        fun fromId(id: String): DisasterType = entries.find { it.id == id } ?: OTHER
    }
}

data class Report(
    val id: String,
    val type: DisasterType,
    val description: String,
    val photoUrl: String,
    val lat: Double,
    val lng: Double,
    val createdAt: Long,
    val reporterId: String,
    val reporterEmail: String,
)
