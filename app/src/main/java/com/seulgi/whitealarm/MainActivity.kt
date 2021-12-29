package com.seulgi.whitealarm

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.seulgi.whitealarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var TAG = MainActivity::class.java.simpleName

    private lateinit var permissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var resolutionResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var binding : ActivityMainBinding

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

    private val tvLatitude: TextView by lazy { findViewById(R.id.tv_latitude) }
    private val tvLongitude: TextView by lazy { findViewById(R.id.tv_longitude) }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0 : LocationResult){
            super.onLocationResult(p0)
            tvLatitude.text = p0.lastLocation.latitude.toString()
            tvLongitude.text = p0.lastLocation.longitude.toString()
        }
        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 권한 //
        val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    Log.d(TAG, "fine")
                    Toast.makeText(this, "위치 정보 사용이 승인되었습니다.", Toast.LENGTH_LONG).show()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    Log.d(TAG, "coarse")
                    Log.d(TAG, Manifest.permission.ACCESS_COARSE_LOCATION)
                } else -> {
                // No location access granted.
                    Toast.makeText(this, "위치 정보 사용이 거부되었습니다. 변경하려면 회원정보에 들어가세요.", Toast.LENGTH_LONG).show()
            }
            }
        }
        locationPermissionRequest.launch(permissions)

        val locationBtn = findViewById<Button>(R.id.locationBtn)
        locationBtn.setOnClickListener {
            getLocation()
        }
    }

    private fun checkPermission(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getLocation() {
        if (checkPermission(permissions)) {
            LocationServices.getSettingsClient(this@MainActivity).checkLocationSettings(builder.build()).run {
                addOnSuccessListener { response ->
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
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