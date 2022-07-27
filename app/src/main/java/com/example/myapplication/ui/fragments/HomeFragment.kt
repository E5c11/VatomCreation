package com.example.myapplication.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.HomeFragmentBinding
import com.example.myapplication.repositories.VatomincRepo
import com.example.myapplication.utils.hasCameraPermission
import com.example.myapplication.utils.hasCoarseLocPermission
import com.example.myapplication.utils.hasFineLocPermission
import com.example.myapplication.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.home_fragment) {

    @Inject lateinit var vatomincRepo: VatomincRepo

    private lateinit var binding : HomeFragmentBinding
    private val viewModel: HomeViewModel by viewModels()

    private val requestCode = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeFragmentBinding.bind(view)

        setListeners()
        requestPermission()
    }

    private fun setListeners() {
        binding.apply {
            login.setOnClickListener { login() }
            vatoms.setOnClickListener { viewModel.getVatoms() }
            ar.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMapFragment()) }
        }
    }

    private fun login() = lifecycleScope.launch(IO) {
        try {
            vatomincRepo.loginOpenID(requireActivity())

            withContext(Main) { binding.vatoms.visibility = View.VISIBLE }

        } catch (err: Throwable) {
            err.stackTrace
            throw err
        }
    }

    private fun requestPermission() {
        if (hasCameraPermission(requireContext()) && hasCoarseLocPermission(requireContext())
            && hasFineLocPermission(requireContext())
        ) binding.ar.visibility = View.VISIBLE
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
            arrayOf(
                Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), requestCode)
    }

}