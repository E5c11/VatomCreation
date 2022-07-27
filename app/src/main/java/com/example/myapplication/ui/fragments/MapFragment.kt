package com.example.myapplication.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.data.LocationBounds
import com.example.myapplication.databinding.MapFragmentBinding
import com.example.myapplication.repositories.VatomincRepo
import com.example.myapplication.utils.hasCameraPermission
import com.example.myapplication.utils.hasCoarseLocPermission
import com.example.myapplication.utils.hasFineLocPermission
import com.example.myapplication.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment: Fragment(R.layout.map_fragment), OnMapReadyCallback {

    @Inject lateinit var vatomincRepo: VatomincRepo

    private lateinit var binding: MapFragmentBinding
    private val viewModel: MapViewModel by viewModels()
    private var mapViewBundle: Bundle? = null
    private val requestCode = 1
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private lateinit var gMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MapFragmentBinding.bind(view)
//        lifecycleScope.launch(IO) { vatomincRepo.loginOpenID(requireActivity()) }
        requestPermission()
        mapSetup(savedInstanceState)
        setObservers()
        binding.arBtn.setOnClickListener {
            findNavController().navigate(MapFragmentDirections.actionMapFragmentToArFragment(
                LocationBounds(gMap.projection.visibleRegion.latLngBounds)
            ))
        }
    }

    private fun setObservers() {
        viewModel.loc.observe(viewLifecycleOwner, {
            binding.arBtn.visibility = View.VISIBLE
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 13.0f))
            viewModel.getRegionVatoms(LocationBounds(gMap.projection.visibleRegion.latLngBounds))
        })
    }

    private fun requestPermission() {
        if (hasCameraPermission(requireContext()) && hasCoarseLocPermission(requireContext())
            && hasFineLocPermission(requireContext())) return
        else askPermission()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            this.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED
                    && !hasCameraPermission(requireContext())
                ) { askPermission() }
                return
            }
        }
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), requestCode)
    }

    private fun mapSetup(savedInstanceState: Bundle?) {
        binding.mapView.apply {
            onCreate(mapViewBundle)
            getMapAsync(this@MapFragment)
            mapViewBundle = null
            if (savedInstanceState != null) mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(gMap: GoogleMap) {
        this.gMap = gMap
        gMap.isMyLocationEnabled = true
        binding.mapView.onResume()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

}