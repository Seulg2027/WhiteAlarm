package com.seulgi.whitealarm.fragments

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.seulgi.whitealarm.R
import com.seulgi.whitealarm.databinding.FragmentHospitalBinding
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HospitalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HospitalFragment : Fragment() {
    private lateinit var binding: FragmentHospitalBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var resolutionResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private var TAG = HospitalFragment::class.java.simpleName

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

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hospital, container, false)

        binding.mainFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_hospitalFragment_to_mainFragment)
        }
        binding.newsFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_hospitalFragment_to_newsFragment)
        }
        binding.mypageFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_hospitalFragment_to_settingFragment)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val mapView = MapView(requireContext())
        binding.mapView.addView(mapView)

        moveToLocation(mapView)

        return binding.root
    }

    private fun checkPermission(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun moveToLocation(mapView : MapView){
        Log.d(TAG, checkPermission(permissions).toString())
        if(checkPermission(permissions)) {
            val lm: LocationManager = getContext()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                val userNowLocation: Location? = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val uLatitude = userNowLocation?.latitude
                val uLongitude = userNowLocation?.longitude
                val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!)

                mapView.setMapCenterPoint(uNowPosition, true)
            } catch (e: NullPointerException) {
                Log.e("LOCATION_ERROR", e.toString())
            }
        } else {
            Toast.makeText(requireContext(), "위치 권한이 없습니다. 설정에서 변경해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

}