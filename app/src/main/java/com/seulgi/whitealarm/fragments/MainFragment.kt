package com.seulgi.whitealarm.fragments

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.seulgi.whitealarm.R
import com.seulgi.whitealarm.databinding.FragmentMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    private var TAG = MainFragment::class.java.simpleName

    private lateinit var permissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var resolutionResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10000
        fastestInterval = 5000
        numUpdates = 10
    }

    private val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .setAlwaysShow(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        binding.newsFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_newsFragment)
        }
        binding.hospitalFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_hospitalFragment)
        }
        binding.mypageFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_settingFragment)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // 위치 권한 //
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    Log.d(TAG, "fine")
                    Toast.makeText(requireContext(), "위치 정보 사용이 승인되었습니다.", Toast.LENGTH_LONG).show()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    Log.d(TAG, "coarse")
                    Log.d(TAG, Manifest.permission.ACCESS_COARSE_LOCATION)
                } else -> {
                // No location access granted.
                Toast.makeText(requireContext(), "위치 정보 사용이 거부되었습니다. 변경하려면 회원정보에 들어가세요.", Toast.LENGTH_LONG).show()
            }
            }
        }
        locationPermissionRequest.launch(permissions)

        binding.locationBtn.setOnClickListener {
            Log.d(TAG, "클릭")
            getLocation(binding.tvLatitude, binding.tvLongitude)
        }

        return binding.root
    }

    inner class LocationCall(tvLatitude : TextView, tvLongitude : TextView) {
        val locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0 : LocationResult){
                super.onLocationResult(p0)
                Log.d(TAG, p0.lastLocation.latitude.toString())
                tvLatitude.text = p0.lastLocation.latitude.toString()
                tvLongitude.text = p0.lastLocation.longitude.toString()
            }
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }
        }
    }

    private fun checkPermission(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getLocation(tvLatitude : TextView, tvLongitude : TextView) {
        if (checkPermission(permissions)) {
            LocationServices.getSettingsClient(requireContext()).checkLocationSettings(builder.build()).run {
                addOnSuccessListener { response ->
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                    Log.d(TAG, "getLocation")
                    fusedLocationClient.requestLocationUpdates(locationRequest, LocationCall(tvLatitude, tvLongitude).locationCallback, Looper.getMainLooper())
                }
                addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                            resolutionResultLauncher.launch(intentSenderRequest) }
                        catch (sendEx: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                }
            }
        } else {
            permissionResultLauncher.launch(permissions)
        }
    }
}