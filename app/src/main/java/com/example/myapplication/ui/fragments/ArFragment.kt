package com.example.myapplication.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.data.LocationBounds
import com.example.myapplication.databinding.ArFragmentBinding
import com.example.myapplication.utils.hasCameraPermission
import com.example.myapplication.viewmodels.ArViewModel
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArFragment: Fragment(R.layout.ar_fragment) {

    private lateinit var binding : ArFragmentBinding
    private val viewModel: ArViewModel by viewModels()
    private lateinit var session: Session
    private var mUserRequestedInstall = true
    private val args: ArFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ArFragmentBinding.bind(view)
//        viewModel.getRegionVatoms(args.location)
    }

    private fun createSession() {
        session = Session(activity)
        val config = Config(session)
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        config.planeFindingMode = Config.PlaneFindingMode.DISABLED
        session.configure(config)
        binding.arScene.setupSession(session)
        binding.arScene.resume()
    }

    override fun onResume() {
        super.onResume()
        try {
            if (!this::session.isInitialized) {
                when (ArCoreApk.getInstance().requestInstall(requireActivity(), mUserRequestedInstall)) {
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        createSession()
                    }
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        mUserRequestedInstall = false
                        return
                    }
                }
            }
        } catch (e: UnavailableUserDeclinedInstallationException) {
            Toast.makeText(requireContext(), "You need to install ArCore", Toast.LENGTH_LONG)
                .show()
            return
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::session.isInitialized) session.close()
        binding.arScene.pause()

    }

}