package com.example.myapplication.data.pojos

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationBounds(val location: LatLngBounds): Parcelable
