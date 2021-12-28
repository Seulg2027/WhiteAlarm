package com.seulgi.whitealarm.news

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seulgi.whitealarm.R

class NewsRVAdapter(
    val context : Context,
    val items : ArrayList<NewsModel>,
    val keyList : ArrayList<String>
    ) : RecyclerView.Adapter<NewsRVAdapter.Viewholder>(){
    private var TAG = NewsRVAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)

        Log.d(TAG, items.toString())
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: NewsRVAdapter.Viewholder, position: Int) {
        //
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

}