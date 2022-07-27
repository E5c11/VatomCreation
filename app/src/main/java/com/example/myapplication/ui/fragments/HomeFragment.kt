package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.HomeFragmentBinding
import com.example.myapplication.repositories.VatomincRepo
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeFragmentBinding.bind(view)

        binding.login.setOnClickListener {
            lifecycleScope.launch(IO) {
                try {
                    // Login now
                    vatomincRepo.loginOpenID(requireActivity())

                    withContext(Main) { binding.vatoms.visibility = View.VISIBLE }

                } catch (err: Throwable) {
                    // Rethrow the error
                    err.stackTrace
                    throw err
                }
            }
        }
        binding.vatoms.setOnClickListener {
            viewModel.getVatoms()
        }
        binding.ar.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMapFragment()) }
    }

}