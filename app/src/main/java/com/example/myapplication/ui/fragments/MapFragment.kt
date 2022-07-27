package com.example.myapplication.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.example.myapplication.R
import com.example.myapplication.data.pojos.LocationBounds
import com.example.myapplication.data.pojos.vatom.Vatom
import com.example.myapplication.databinding.MapFragmentBinding
import com.example.myapplication.repositories.VatomincRepo
import com.example.myapplication.utils.hasCameraPermission
import com.example.myapplication.utils.hasCoarseLocPermission
import com.example.myapplication.utils.hasFineLocPermission
import com.example.myapplication.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class MapFragment: Fragment(R.layout.map_fragment), OnMapReadyCallback {

    @Inject lateinit var vatomincRepo: VatomincRepo

    private lateinit var binding: MapFragmentBinding
    private val viewModel: MapViewModel by viewModels()
    private var mapViewBundle: Bundle? = null
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private lateinit var gMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MapFragmentBinding.bind(view)
//        lifecycleScope.launch(IO) { vatomincRepo.loginOpenID(requireActivity()) }
        mapSetup(savedInstanceState)

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.arBtn.setOnClickListener {
            findNavController().navigate(MapFragmentDirections.actionMapFragmentToArFragment(
                LocationBounds(gMap.projection.visibleRegion.latLngBounds)
            ))
        }
    }

    private fun setObservers() {
        viewModel.apply {
            loc.observe(viewLifecycleOwner) {
                binding.arBtn.visibility = View.VISIBLE
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 13.0f))
            }
            vatoms.observe(viewLifecycleOwner) { setMarkers(it) }
        }
    }

    private fun setMarkers(vatoms: List<Vatom>) = lifecycleScope.launch(IO) {
        for (vatom in vatoms) {
            val marker = withContext(Main) {
                gMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(vatom.property.geoPos!!.coordinates[1],
                                vatom.property.geoPos!!.coordinates[0]))
                        .title(vatom.property.title)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
            }
            setMarkerIcon(marker!!, vatom.property.resources[0].value!!.value!!)
        }
    }

    private suspend fun setMarkerIcon(marker: Marker, imageUrl: String) {
        val bitmap = withContext(IO) {
             Glide.with(binding.root).asBitmap().load(imageUrl).fitCenter().submit().get()
        }
        val smallMarker = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        withContext(Main) { marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker)) }
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

        // Get vatoms in screen region after camera moved
        gMap.setOnCameraIdleListener {
            viewModel.getRegionVatoms(LocationBounds(gMap.projection.visibleRegion.latLngBounds))
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

}