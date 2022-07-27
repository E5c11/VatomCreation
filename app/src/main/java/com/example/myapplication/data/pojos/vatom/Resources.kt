package com.example.myapplication.data.pojos.vatom

import com.google.gson.annotations.SerializedName

data class Resources (

    @SerializedName("name"         ) var name         : String? = null,
    @SerializedName("resourceType" ) var resourceType : String? = null,
    @SerializedName("value"        ) var value        : Value?  = Value()

)
