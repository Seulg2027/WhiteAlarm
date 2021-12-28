package com.seulgi.whitealarm.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.seulgi.whitealarm.R
import com.seulgi.whitealarm.auth.IntroActivity
import com.seulgi.whitealarm.databinding.FragmentSettingBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)

        binding.mainFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_settingFragment_to_mainFragment)
        }
        binding.newsFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_settingFragment_to_newsFragment)
        }
        binding.hospitalFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_settingFragment_to_hospitalFragment)
        }

        binding.logoutBtn.setOnClickListener {
            auth.signOut()

            val intent = Intent(context, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return binding.root
    }

}