package com.example.myapplication.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.pojos.LocationBounds
import com.example.myapplication.repositories.VatomincRepo
import com.example.myapplication.data.persistence.UserPreferences
import com.example.myapplication.data.pojos.vatom.Vatom
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    val app: Application,
    private val vatominc: VatomincRepo,
    private val preferences: UserPreferences
): AndroidViewModel(app) {

    private var requestingLocationUpdates = false
    private val _loc = MutableLiveData<LatLng>()
    val loc = _loc
    private val _vatoms = MutableLiveData<List<Vatom>>()
    val vatoms = _vatoms
    private val pref = preferences.userPref

    var latLng = LatLng(0.0, 0.0)

    private var locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fastestInterval = 500
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.locations.size > 0) {
//        lifecycleScope.launch(IO) { vatomincRepo.loginOpenID(requireActivity()) }
                val latestLocationIndex = locationResult.locations.size -1
                val lat = locationResult.locations[latestLocationIndex].latitude
                val lng = locationResult.locations[latestLocationIndex].longitude
                if (lat != 0.0) {
                    LocationServices.getFusedLocationProviderClient(app).removeLocationUpdates(this)
                    latLng = LatLng(lat, lng)
                    Log.d("myT", "onLocationResult: $latLng")
                    _loc.value = latLng
                }
            }
        }
    }

    init {
        startLocationUpdate()
    }

    fun getRegionVatoms(locationBounds: LocationBounds) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("myT", "southWest: ${locationBounds.location.southwest}")
        Log.d("myT", "southWest: ${locationBounds.location.northeast}")
        pref.collect {
            val list = vatominc.getRegionVatoms(it.blockvToken, locationBounds)
            _vatoms.postValue(list!!)
            list.forEach{ vatom ->
                Log.d("myT", "getRegionVatoms: ${vatom.property.geoPos?.coordinates}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdate() {
        requestingLocationUpdates = true
        LocationServices.getFusedLocationProviderClient(app)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

}