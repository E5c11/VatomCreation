package com.example.myapplication.data.pojos.vatom

import com.example.myapplication.data.pojos.Coord

data class RegionBody(
    val bottom_left: Coord,
    val top_right: Coord,
    val filter: String,
    val limit: Int
)
