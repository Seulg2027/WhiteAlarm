package com.seulgi.whitealarm.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.seulgi.whitealarm.R
import com.seulgi.whitealarm.auth.SignupActivity
import com.seulgi.whitealarm.databinding.FragmentHospitalBinding
import com.seulgi.whitealarm.hospital.ExampleActivity
import com.seulgi.whitealarm.hospital.KakaoAPI
import com.seulgi.whitealarm.hospital.ResultSearchKeyword
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    // GPS //
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var resolutionResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private var TAG = HospitalFragment::class.java.simpleName

    private lateinit var locationManager: LocationManager

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

    // 병원 검색 //
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK e5424acbaac76f6c09c5b54953d7df06"
    }
    private lateinit var x : String
    private lateinit var y : String

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

        binding.locationBtn.setOnClickListener {
            if (checkLocationService()) {
                x = "100"
                y = "10"
                moveToLocation(mapView)

                searchKeyword("병원", x, y)
            } else {
                Toast.makeText(requireContext(), "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun changeLocation(new_x: String, new_y: String) {
        x = new_x
        y = new_y
    }

    inner class LocationCall(mapView : MapView) {
        val locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0 : LocationResult){
                super.onLocationResult(p0)
                changeLocation(p0.lastLocation.latitude.toString(), p0.lastLocation.longitude.toString())
                Log.d(TAG, p0.lastLocation.latitude.toString())
                Toast.makeText(requireContext(), "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(p0.lastLocation.latitude, p0.lastLocation.longitude), true)
                startTracking(mapView)

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

    private fun moveToLocation(mapView : MapView){
        if(checkPermission(permissions)) {
            LocationServices.getSettingsClient(requireContext()).checkLocationSettings(builder.build()).run {
                addOnSuccessListener { response ->
                    fusedLocationClient.requestLocationUpdates(locationRequest, LocationCall(mapView).locationCallback, Looper.getMainLooper())
                }
                addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        try {
                            val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                            resolutionResultLauncher.launch(intentSenderRequest) }
                        catch (sendEx: IntentSender.SendIntentException) {

                        }
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "위치 권한이 없습니다. 설정에서 변경해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 키워드 검색 //
    private fun searchKeyword(keyword: String, x: String, y: String){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchKeyword(API_KEY, keyword, x, y)

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // api 통신 성공
                Log.d(TAG, "Raw: ${response.raw()}")
                Log.d(TAG, "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w(TAG, "통신 실패: ${t.message}")
            }
        })
    }


    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        if (::locationManager.isInitialized.not()) {
            locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun startTracking(mapView : MapView) {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }
}