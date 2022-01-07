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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.seulgi.whitealarm.R
import com.seulgi.whitealarm.auth.SignupActivity
import com.seulgi.whitealarm.databinding.FragmentHospitalBinding
import com.seulgi.whitealarm.hospital.*
import net.daum.mf.map.api.MapPOIItem
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
    private lateinit var rvAdapter : HospitalRVAdapter
    private var listItems = arrayListOf<HospitalListLayout>()

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

        // RecyclerView
        rvAdapter = HospitalRVAdapter(requireContext(), listItems)

        binding.locationBtn.setOnClickListener {
            if (checkLocationService()) {
                moveToLocation(binding.mapView)
            } else {
                Toast.makeText(requireContext(), "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }
        binding.stopBtn.setOnClickListener {
            stopTracking()
        }

        val recycler : RecyclerView = binding.rv
        recycler.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recycler.adapter = rvAdapter

        rvAdapter.setItemClickListener(object: HospitalRVAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int){
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1,true)
            }
        })

        return binding.root
    }

    inner class LocationCall(mapView : MapView) {
        val locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0 : LocationResult){
                super.onLocationResult(p0)
                Log.d(TAG, p0.lastLocation.latitude.toString())
                Log.d(TAG, p0.lastLocation.longitude.toString())
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(p0.lastLocation.latitude, p0.lastLocation.longitude), true)
                startTracking()
                searchKeyword("병원", p0.lastLocation.latitude.toString(), p0.lastLocation.longitude.toString())
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
        val call = api.getSearchKeyword(API_KEY, "126", x, keyword)

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // api 통신 성공
                Log.d(TAG, "Raw: ${response.raw()}")
                Log.d(TAG, "Body: ${response.body()}")
                setItems(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w(TAG, "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 처리 //
    private fun setItems(searchResult: ResultSearchKeyword?){
        if(!searchResult?.documents.isNullOrEmpty()) {
            listItems.clear()
            binding.mapView.removeAllPolylines()
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = HospitalListLayout(document.place_name,
                        document.road_address_name,
                        document.address_name,
                        document.x.toDouble(),
                        document.y.toDouble())
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                            document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(point)
            }
            rvAdapter.notifyDataSetChanged()

        } else {
            Toast.makeText(requireContext(), "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }


    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        if (::locationManager.isInitialized.not()) {
            locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun startTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    private fun stopTracking() {
        binding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }
}