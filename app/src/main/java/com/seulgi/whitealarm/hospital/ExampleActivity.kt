package com.seulgi.whitealarm.hospital

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.seulgi.whitealarm.R
import com.seulgi.whitealarm.databinding.ActivityExampleBinding
import net.daum.mf.map.api.MapView

class ExampleActivity : AppCompatActivity() {
    private lateinit var binding : ActivityExampleBinding
    private val TAG = ExampleActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_example)


    }


}