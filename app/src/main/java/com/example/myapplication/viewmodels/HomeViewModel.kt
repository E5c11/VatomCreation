package com.example.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repositories.VatomincRepo
import com.example.myapplication.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tempRepo: VatomincRepo,
    private val templatePref: UserPreferences
): ViewModel() {

    fun getVatoms() = viewModelScope.launch(Dispatchers.IO) {
        templatePref.userPref.collect {
            val vatoms = tempRepo.getVatoms(accessToken = it.blockvToken)
        }
    }


}