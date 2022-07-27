package com.example.myapplication.data

import com.google.android.gms.maps.model.LatLng

data class RegionBody(
    val bottom_left: Coord,
    val top_right: Coord,
    val filter: String,
    val limit: Int
)
