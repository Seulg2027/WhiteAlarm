package com.seulgi.whitealarm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.seulgi.whitealarm.R
import com.seulgi.whitealarm.databinding.FragmentHospitalBinding

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

        return binding.root
    }

}