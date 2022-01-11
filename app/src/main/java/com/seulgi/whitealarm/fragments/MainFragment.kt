package com.seulgi.whitealarm.fragments

import android.Manifest
import android.content.Context
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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.seulgi.whitealarm.main.VisitModel
import com.seulgi.whitealarm.main.VisitRVAdapter
import com.seulgi.whitealarm.util.FBRef
import com.seulgi.whitealarm.util.FBauth

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
    lateinit var rvAdapter: VisitRVAdapter
    val keyList = mutableListOf<String>()
    val items = ArrayList<VisitModel>()

    private var TAG = MainFragment::class.java.simpleName

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

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

        getVisitUidData() // 사용자의 visit 정보를 불러옴

        // adapter //
        rvAdapter = VisitRVAdapter(requireContext(), items)

        val recycler : RecyclerView = binding.rv
        val emptyView : TextView = binding.emptyView

        recycler.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recycler.adapter = rvAdapter


        // 위치 권한 //
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    Log.d(TAG, "fine")
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

        return binding.root
    }

    private fun showPopup(place_name : String) {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_popup, null)
        val textView: TextView = view.findViewById(R.id.textView)
        textView.text = "${place_name}에 방문한지 7일이 지났습니다. 계속 방문하시겠습니까?"

        val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("병원 방문 알림")
                .setPositiveButton("예") { dialog, which ->
                    Toast.makeText(context, "알림 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .setNeutralButton("아니오", null)
                .create()

        alertDialog.setView(view)
        alertDialog.show()
    }

    private fun getVisitUidData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (dataModel in dataSnapshot.children){
                    val item = dataModel.getValue(VisitModel::class.java)
                    Log.d(TAG, FBauth.diffTime(item!!.getdateData()).toString())

                    if (FBauth.diffTime(item!!.getdateData()) >= 7){
                        showPopup(item!!.getPlaceName())
                    }
                    items.add(item!!)
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        Log.e(TAG, FBauth.getUid())
        FBRef.visit
                .child(FBauth.getUid())
                .addValueEventListener(postListener)
    }

}