package com.example.myapplication.data.pojos.vatom

import com.google.gson.annotations.SerializedName

data class Value (

    @SerializedName("resourceValueType" ) var resourceValueType : String? = null,
    @SerializedName("value"             ) var value             : String? = null

)
