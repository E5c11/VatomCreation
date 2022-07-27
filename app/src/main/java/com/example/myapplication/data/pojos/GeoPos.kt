package com.example.myapplication.data.pojos

import com.google.gson.annotations.SerializedName

data class GeoPos (

    @SerializedName("type"        ) var type        : String?           = null,
    @SerializedName("coordinates" ) var coordinates : ArrayList<Double> = arrayListOf()

)