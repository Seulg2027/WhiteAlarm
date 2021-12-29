package com.seulgi.whitealarm.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.seulgi.whitealarm.R
import com.seulgi.whitealarm.databinding.FragmentNewsBinding
import com.seulgi.whitealarm.news.NewsModel
import com.seulgi.whitealarm.news.NewsRVAdapter
import com.seulgi.whitealarm.util.FBRef

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding

    lateinit var rvAdapter: NewsRVAdapter

    private var TAG = NewsFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false)

        binding.mainFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_newsFragment_to_mainFragment)
        }
        binding.hospitalFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_newsFragment_to_hospitalFragment)
        }
        binding.mypageFM.setOnClickListener {
            it.findNavController().navigate(R.id.action_newsFragment_to_settingFragment)
        }

        var items = ArrayList<NewsModel>()
        rvAdapter = NewsRVAdapter(requireContext(), items)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (dataModel in dataSnapshot.children){
                    Log.d(TAG, dataModel.toString())

                    val item = dataModel.getValue(NewsModel::class.java)
                    items.add(item!!)
                }
                rvAdapter.notifyDataSetChanged() // 밑에 함수들이 먼저 실행되고 데이터가 나중에 불러와지기 때문에 동기화가 필요
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.news.addValueEventListener(postListener)

        val recycler : RecyclerView = binding.rv

        recycler.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recycler.adapter = rvAdapter


//        FBRef.news.push().setValue(
//                NewsModel(
//
//                )
//        )



        return binding.root
    }

}