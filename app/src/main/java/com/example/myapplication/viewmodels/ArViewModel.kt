package com.example.myapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.pojos.LocationBounds
import com.example.myapplication.repositories.VatomincRepo
import com.example.myapplication.data.persistence.UserPreferences
import kotlinx.coroutines.flow.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArViewModel @Inject constructor(
    val app: Application,
    private val vatominc: VatomincRepo,
    preferences: UserPreferences
): AndroidViewModel(app) {

    private val pref = preferences.userPref

    fun getRegionVatoms(locationBounds: LocationBounds) = viewModelScope.launch(IO) {
        pref.collect {
            vatominc.getRegionVatoms(it.blockvToken, locationBounds)
        }
    }



}