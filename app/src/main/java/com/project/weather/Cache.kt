package com.project.weather

import kotlinx.coroutines.flow.MutableStateFlow
import org.osmdroid.util.GeoPoint

object Cache {
    var FavoriteLocationPoint: MutableStateFlow<GeoPoint?> = MutableStateFlow(null)
}