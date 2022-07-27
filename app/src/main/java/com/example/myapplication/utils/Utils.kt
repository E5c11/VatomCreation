package com.example.myapplication.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.NumberPicker
import androidx.core.content.ContextCompat

fun setPickerValues(picker: NumberPicker, values: Array<String>, max: Int, displayVal: Int) =
    picker.apply { displayedValues = values; maxValue = max; value = displayVal }

fun hasCameraPermission(context: Context) =
    (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED)

fun hasCoarseLocPermission(context: Context) =
    (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)

fun hasFineLocPermission(context: Context) =
    (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)